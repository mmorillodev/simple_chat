package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<ClientInfo> clients;
    private boolean isRunning;
    private String name;

    private ServerSocket server;

    public Server() {
        server = null;
        isRunning = true;
        ScannerUtils scanner = new ScannerUtils();
        clients = new ArrayList<>();

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
        String message = "";
        name = scanner.getString("Type your name: ");

        new Thread(new KeepWaitingClient()).start();

//        cls();

        while (!message.equals("!SHUTDOWN")) {
            message = scanner.getString("").trim();
            sendMessageToClients(name + ": " + message, null);
        }

        isRunning = false;
    }

    private void sendMessageToClients(String message, ClientInfo except) {
        if(except == null) {
            for (ClientInfo client : clients) {
                client.sendMessage(message);
            }
        }
        else {
            for(ClientInfo client : clients) {
                if(client == except) continue;
                client.sendMessage(message);
            }
        }
    }

    private void cls() {
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }
    }

    private class KeepWaitingClient implements Runnable {

        @Override
        public void run() {
            try {
                getClients();
             }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        void getClients() throws IOException {
            while (isRunning) {
                ClientInfo sClient = new ClientInfo(server.accept());
                clients.add(sClient);
                System.out.println(name + " connected");
                sClient.sendMessage("Connected to " + name);
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
                    System.out.println(message);
                    sendMessageToClients(message, client);
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

            Thread messageReader = new Thread(new WaitMessages(this));
            messageReader.setName("messageReader-"+name);
            messageReader.start();
        }

        void sendMessage(String message) {
            writer.println(message);
            writer.flush();
        }
    }
}