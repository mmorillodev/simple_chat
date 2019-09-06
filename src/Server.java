import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private final List<ClientInfo> clients;

    private ServerSocket server;

    private Server() {
        clients = new ArrayList<>();
        server = null;
        try {
            server = new ServerSocket(8080);
        }
        catch(IOException e) {
            System.err.println("Port already in use!");
        }
    }

    public static void main(String[] args) {
        new Server().init();
    }

    private void init() {
        new Thread(new KeepWaitingClients()).run();
    }

    private class KeepWaitingClients implements Runnable {

        @Override
        public void run() {
            boolean error = false;
            while(!error) {
                try {
                    clients.add(new ClientInfo(server.accept()));
                    System.out.println("Client connected: " + clients.get(clients.size() - 1).name);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    error = true;
                }
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
            boolean error = false;

            while(!error) {
                try {
                    sendToOthers(client, client.name + ": " + client.reader.readLine());
                }
                catch (IOException e) {
                    e.printStackTrace();
                    error = true;
                }
            }
        }

        void sendToOthers(ClientInfo client, String message) {
            for (ClientInfo client_ : clients) {
                if(client_ == client) continue;
                client_.writer.println(message);
                client_.writer.flush();
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

            new Thread(new WaitMessages(this)).run();
        }
    }
}


