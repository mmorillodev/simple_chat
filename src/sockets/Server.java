package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Server {

    private List<ClientInfo> clients;

    private ServerSocket server;

    public Server() {
        server = null;
        clients = new LinkedList<>();
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
        String message = "";
        String name = scanner.getString("Type your name: ");

        new Thread(new KeepWaitingClient()).start();

        while (!message.equals("!SHUTDOWN")) {
            message = scanner.getString("").trim();
            if(message.equals("!CLS")) {
                cls();
                continue;
            }
            if(message.length() == 0)
                continue;

            sendMessageToClients(name + ": " + message, null);
        }

        System.exit(0);
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
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    private class KeepWaitingClient implements Runnable {

        private boolean keepWaiting;

        KeepWaitingClient() {
            keepWaiting = true;
        }

        @Override
        public void run() {
            try {
                getClients();
             }
            catch (IOException e) {
                System.out.println("Unexpected error thrown: " + e.getMessage());
                keepWaiting = false;
            }
        }

        void getClients() throws IOException {
            while (keepWaiting) {
                ClientInfo sClient = new ClientInfo(server.accept());
                clients.add(sClient);
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
            boolean isRunning = true;
            String message;

            while(isRunning) {
                try {
                    message = client.reader.readLine();
                    System.out.println(message);
                    sendMessageToClients(message, client);
                }
                catch (IOException e) {
                    clients.remove(client);
                    System.out.println("Client disconnected!");
                }
            }
        }
    }
    class ClientInfo {
        Socket clientSocket;
        PrintWriter writer;
        BufferedReader reader;
//        String name;

        ClientInfo(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.reader       = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer       = new PrintWriter(clientSocket.getOutputStream());
//            this.name         = reader.readLine();

            System.out.println("Client connected!");

            Thread messageReader = new Thread(new Server.WaitMessages(this));
            messageReader.setName("messageReader-"+toString());
            messageReader.start();
        }

        void sendMessage(String message) {
            writer.println(message);
            writer.flush();
        }
    }
}