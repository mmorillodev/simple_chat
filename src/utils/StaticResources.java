package utils;

public class StaticResources {

    public static final String MENU_OPTIONS;
    public static final String SERVER_PORT_REQUEST_MSG;
    public static final String PORT_OCCUPIED_ERROR;
    public static final String DISCONNECTED_CLIENT_MSG;
    public static final String CONNECTED_CLIENT_MSG;
    public static final String SERVER_DISCONNECTED_MSG;
    public static final String IP_REQUEST_MSG;
    public static final String NAME_REQUEST_MSG;
    public static final String CONNECTION_PORT_REQUEST_MSG;
    public static final String CONNECTION_REFUSED_MSG;
    public static final String SERVER_NOT_FOUND_ERROR_PREFIX;
    public static final String EXCEPTION_THROWN_PREFIX;
    public static final String SHUTDOWN_SERVER_PREFIX;
    public static final String LEAVE_SERVER_PREFIX;
    public static final String CLEAR_CLI_PREFIX;
    public static final String CHANGE_NICKNAME_PREFIX;

    static {
        MENU_OPTIONS                    = "[S]erver or [C]lient? ";
        SERVER_PORT_REQUEST_MSG         = "Enter the port the server will listen: ";
        PORT_OCCUPIED_ERROR             = "Port already in use!";
        DISCONNECTED_CLIENT_MSG         = "Client disconnected!";
        CONNECTED_CLIENT_MSG            = "Client connected!";
        SERVER_DISCONNECTED_MSG         = "Server disconnected!";
        IP_REQUEST_MSG                  = "Enter the IP to connect: ";
        NAME_REQUEST_MSG                = "Type your name: ";
        CONNECTION_REFUSED_MSG          = "Connection refused";
        CONNECTION_PORT_REQUEST_MSG     = "Enter the port to connect: ";
        EXCEPTION_THROWN_PREFIX         = "Unexpected error thrown: ";
        SERVER_NOT_FOUND_ERROR_PREFIX   = "Server not found! ";
        LEAVE_SERVER_PREFIX             = "!EXIT";
        SHUTDOWN_SERVER_PREFIX          = "!SHUTDOWN";
        CLEAR_CLI_PREFIX                = "!CLS";
        CHANGE_NICKNAME_PREFIX          = "!CHANGE";
    }
}
