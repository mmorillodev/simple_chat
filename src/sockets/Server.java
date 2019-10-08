package sockets;

import utils.ScannerUtils;
import utils.StaticResources;
import static utils.StaticResources.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import static java.lang.System.out;

public class Server {

    private List<ClientInfo> clients;
    private ServerSocket server;
    private String name;

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
            name = scanner.getString(NAME_REQUEST_MSG);
        }
        catch(IOException e) {
            System.err.println(PORT_OCCUPIED_ERROR);
        }
    }

    public void init() {
        ScannerUtils scanner = new ScannerUtils();
        String message = "";

        new Thread(new KeepWaitingClient()).start();

        while (!message.equalsIgnoreCase(SHUTDOWN_SERVER_PREFIX)) {
            message = scanner.getString("").trim();
            if(message.length() != 0) {
                if (message.equalsIgnoreCase(StaticResources.CLEAR_CLI_PREFIX)) {
                    cls();
                }
                else if (message.equalsIgnoreCase(CHANGE_NICKNAME_PREFIX)) {
                    message = this.name + " has changed its nickname to " + (this.name = scanner.getString(NAME_REQUEST_MSG));
                    sendMessageToClients(message, null);
                }
                else {
                    sendMessageToClients(name + ": " + message, null);
                }
            }
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
            out.println();
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
                out.println(EXCEPTION_THROWN_PREFIX + e.getMessage());
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
                    out.println(message);
                    sendMessageToClients(message, client);
                }
                catch (IOException e) {
                    clients.remove(client);
                    out.println(DISCONNECTED_CLIENT_MSG);
                    isRunning = false;
                }
            }
        }
    }
    class ClientInfo {
        Socket clientSocket;
        PrintWriter writer;
        BufferedReader reader;

        ClientInfo(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.reader       = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer       = new PrintWriter(clientSocket.getOutputStream());

            out.println(CONNECTED_CLIENT_MSG);

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