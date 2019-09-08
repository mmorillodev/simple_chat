package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket client;

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
//        writer.print(name);
//        writer.flush();

        Thread messageReader = new Thread(this);
        messageReader.setName("messageReader");
        messageReader.start();

        while(!currentText.equals("!EXIT")) {
            currentText = scanner.getString("").trim();
            if(currentText.equals("!CLS")) {
                cls();
                continue;
            }
            if(currentText.length() == 0)
                continue;

            writer.println(name + ": " + currentText.trim());
            writer.flush();
        }

        System.exit(0);
    }

    @Override
    public void run() {
        try {
            readMessages();
        }
        catch (IOException e) {
            System.out.println("Server disconnected!");
        }
    }

    private void readMessages() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message;

        while(true) {
            message = reader.readLine();
            System.out.println(message);
        }
    }

    private void cls() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }
}
