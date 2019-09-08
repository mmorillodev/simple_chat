package sockets;

import utils.ScannerUtils;
import utils.StaticResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static utils.StaticResources.*;

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
                            SERVER_PORT_REQUEST_MSG,
                            n -> n >= 0 && n < 49152
                    )
            );
            scanner.clearBuffer();
        }
        catch(IOException e) {
            System.err.println(PORT_OCCUPIED_ERROR);
        }
    }

    public void init() {
        ScannerUtils scanner = new ScannerUtils();
        String message = "";
        String name = scanner.getString(NAME_REQUEST_MSG);

        new Thread(new KeepWaitingClient()).start();

        while (!message.equals(SHUTDOWN_SERVER_PREFIX)) {
            message = scanner.getString("").trim();
            if(message.equals(StaticResources.CLEAR_CLI_PREFIX)) {
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
                System.out.println(EXCEPTION_THROWN_PREFIX + e.getMessage());
                keepWaiting = false;
            }
        }

        void getClients() throws IOException {
            while (keepWaiting) {
                clients.add(new ClientInfo(server.accept()));
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
                    System.out.println(DISCONNECTED_CLIENT_MSG);
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

            System.out.println(CONNECTED_CLIENT_MSG);

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