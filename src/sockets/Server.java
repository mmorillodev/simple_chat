package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ClientInfo client;
    private boolean isRunning;

    private ServerSocket server;

    public Server() {
        client = null;
        server = null;
        isRunning = true;
        ScannerUtils scanner = new ScannerUtils();

        try {
            server = new ServerSocket(
                    scanner.getInt(
                            "Enter the port the server will listen: ",
                            n -> n >= 0 && n < 49152
                    )
            );
            scanner.clearBuffer();
        }
        catch(IOException e) {
            System.err.println("Port already in use!");
        }
    }

    public void init() {
        ScannerUtils scanner = new ScannerUtils();
        String message = scanner.getString("Type your name: ");

        new KeepWaitingClient().run();

        client.writer.println("Connected to " + message + "!");
        client.writer.flush();

        while (!message.equals("!SHUTDOWN")) {
            message = scanner.getString("");
            client.writer.println(message.trim() + "\n");
            client.writer.flush();
        }

        isRunning = false;
    }

    private class KeepWaitingClient implements Runnable {

        @Override
        public void run() {
            try {
                Socket sClient = server.accept();
                client = new ClientInfo(sClient);
             }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class WaitMessages implements Runnable {
        ClientInfo client;

        WaitMessages(ClientInfo client) {
            this.client = client;
        }

        @Override
        public void run() {
            String message;
            while(isRunning) {
                try {
                    message = client.reader.readLine();
                    System.out.println(client.name + ": " + message);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    isRunning = false;
                }
            }
        }
    }

    private class ClientInfo {
        String          name;
        Socket          clientSocket;
        PrintWriter     writer;
        BufferedReader  reader;

        ClientInfo(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.reader       = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer       = new PrintWriter(clientSocket.getOutputStream());
            this.name         = reader.readLine();

            System.out.println(name + " connected!");

            Thread messageReader = new Thread(new WaitMessages(this));
            messageReader.setName("messageReader");
            messageReader.start();
        }
    }
}


