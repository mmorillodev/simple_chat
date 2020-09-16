package utils;

import sockets.Client;
import sockets.Server;

import java.util.HashMap;
import java.util.Map;

public class ConnectionUtils {
    private Map<Character, Class> handlers;

    public ConnectionUtils() {
        handlers = new HashMap() {{
            put('s', ServerConnectionHandler.class);
            put('c', ClientConnectionHandler.class);
        }};
    }

    public void connect(char option) throws IllegalAccessException, InstantiationException {
        ((ConnectionHandler) this.handlers.get(option).newInstance()).connect();
    }
}

interface ConnectionHandler {
    void connect();
}

class ServerConnectionHandler implements ConnectionHandler {

    @Override
    public void connect() {
        new Server().init();
    }
}

class ClientConnectionHandler implements ConnectionHandler {

    @Override
    public void connect() {
        new Client().init();
    }
}
