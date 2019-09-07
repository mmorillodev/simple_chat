package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket client;
    private boolean isRunning;

    private final ScannerUtils scanner;

    public Client() {
        scanner = new ScannerUtils();
        try {
            client = new Socket(
                    scanner.getString("Enter the IP to connect: "),
                    scanner.getInt("Enter the port to connect: ", n -> n >= 0 && n < 49152)
            );
            scanner.clearBuffer();
        }
        catch (IOException e) {
            if (e.getMessage().contains("Connection refused")) {
                System.err.println("sockets.Server not found! " + e.getMessage());
            }
        }
    }

    public void init() throws IOException {
        PrintWriter writer = new PrintWriter(client.getOutputStream());
        String currentText = "";

        String name = scanner.getString("Type your name: ");

        Thread messageReader = new Thread(this);
        messageReader.setName("messageReader");
        messageReader.start();

        while(!currentText.equals("!EXIT")) {
            currentText = scanner.getString("");
            writer.println(name + ": " + currentText.trim());
            writer.flush();
        }

        isRunning = false;
    }

    @Override
    public void run() {
        try {
            readMessages();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException {
        isRunning = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message;

//        cls();

        String serverName = reader.readLine();
        System.out.println("Connected to " + serverName);

        while(isRunning) {
            message = reader.readLine();
            System.out.println(message);
        }
    }

    private void cls() {
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }
    }
}
