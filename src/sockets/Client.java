package sockets;

import utils.ScannerUtils;
import static utils.StaticResources.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static java.lang.System.out;

public class Client implements Runnable {

    private Socket client;
    private String name;
    private PrintWriter writer;

    private final ScannerUtils scanner;

    public Client() {
        scanner = new ScannerUtils();
        try {
            client = new Socket(
                    scanner.getString(IP_REQUEST_MSG),
                    scanner.getInt(CONNECTION_PORT_REQUEST_MSG, n -> n >= 0 && n < 49152)
            );
            scanner.clearBuffer();

            this.name = scanner.getString(NAME_REQUEST_MSG);
            this.writer = new PrintWriter(client.getOutputStream());
        }
        catch (IOException e) {
            if (e.getMessage().contains(CONNECTION_REFUSED_MSG)) {
                System.err.println(SERVER_NOT_FOUND_ERROR_PREFIX + e.getMessage());
            }
        }
    }

    public void init() {
        String currentText = "";

        Thread messageReader = new Thread(this);
        messageReader.setName("messageReader");
        messageReader.start();

        while(!currentText.equalsIgnoreCase(LEAVE_SERVER_PREFIX)) {
            currentText = scanner.getString("").trim();

            if(currentText.length() != 0) {
                if (currentText.equalsIgnoreCase(CLEAR_CLI_PREFIX)) {
                    cls();
                }
                else if (currentText.equalsIgnoreCase(CHANGE_NICKNAME_PREFIX)) {
                    currentText = this.name + " has changed its nickname to " + (this.name = scanner.getString(NAME_REQUEST_MSG));
                    sendMessage(currentText);
                }
                else {
                    sendMessage(currentText);
                }
            }
        }

        System.exit(0);
    }

    @Override
    public void run() {
        try {
            readMessages();
        }
        catch (IOException e) {
            out.println(SERVER_DISCONNECTED_MSG);
        }
    }

    private void sendMessage(String message) {
        this.writer.println(this.name + ": " + message.trim());
        this.writer.flush();
    }

    private void readMessages() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message;

        while(true) {
            message = reader.readLine();
            out.println(message);
        }
    }

    private void cls() {
        for (int i = 0; i < 100; i++) {
            out.println();
        }
    }
}
