import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    public static void main(String[] args) {
        try {
            new Client().init();
        }
        catch (IOException e){}
    }

    public void init() throws IOException {
        ScannerUtils scanner = new ScannerUtils();
        Socket client = new Socket("localhost",8080);
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

    }
}
