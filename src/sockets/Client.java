package sockets;

import utils.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static utils.StaticResources.*;

public class Client implements Runnable {

    private Socket client;

    private final ScannerUtils scanner;

    public Client() {
        scanner = new ScannerUtils();
        try {
            client = new Socket(
                    scanner.getString(IP_REQUEST_MSG),
                    scanner.getInt(CONNECTION_PORT_REQUEST_MSG, n -> n >= 0 && n < 49152)
            );
            scanner.clearBuffer();
        }
        catch (IOException e) {
            if (e.getMessage().contains(CONNECTION_REFUSED_MSG)) {
                System.err.println(SERVER_NOT_FOUND_ERROR_PREFIX + e.getMessage());
            }
        }
    }

    public void init() throws IOException {
        PrintWriter writer = new PrintWriter(client.getOutputStream());
        String currentText = "";

        String name = scanner.getString(NAME_REQUEST_MSG);

        Thread messageReader = new Thread(this);
        messageReader.setName("messageReader");
        messageReader.start();

        while(!currentText.equals(LEAVE_SERVER_PREFIX)) {
            currentText = scanner.getString("").trim();
            if(currentText.equals(CLEAR_CLI_PREFIX)) {
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
            System.out.println(SERVER_DISCONNECTED_MSG);
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
