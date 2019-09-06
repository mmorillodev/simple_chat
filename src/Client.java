import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket client;

    public static void main(String[] args) {
        try {
            new Client().init();
        }
        catch (IOException e){
            if(e.getMessage().contains("Connection refused")) {
                System.err.println("Server not found! " + e.getMessage());
            }
        }
    }

    private void init() throws IOException {
        ScannerUtils scanner = new ScannerUtils();
        client = new Socket("localhost",8080);
        PrintWriter writer = new PrintWriter(client.getOutputStream());

        String currentText = scanner.getString("Type your name: ");
        new Thread(this).run();

        while(!currentText.equals("!EXIT")) {
            writer.println(currentText);
            writer.flush();
            currentText = scanner.getString("-> ");
        }
    }

    @Override
    public void run() {
        boolean error = false;
        BufferedReader reader;
        while(!error) {
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                System.out.println(reader.readLine());
            }
            catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        }
    }
}
