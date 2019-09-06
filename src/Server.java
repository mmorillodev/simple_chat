import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private final List<Socket> clients;
    private final ServerSocket server;

    public Server() {
        clients = new ArrayList<>();
        server = null;
        try {
            server = new ServerSocket(8080);
        }
        catch(IOException e) {
            System.err.println("Port already in use!");
        }
        finally {
            if(server != null) {
                try {
                    server.close();
                }
                catch (IOException e){}
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().main();
    }

    public void main() {
        new Thread(this).run();
    }

    @Override
    public void run() {
        try {
            clients.add(server.accept());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
