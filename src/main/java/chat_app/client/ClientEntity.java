package chat_app.client;

import chat_app.message.ChatMessage;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Chat-Client
 */
class ClientEntity {
    private static final Logger LOG = Logger.getLogger(ClientEntity.class);

    ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;

    /**
     * IP or address to the server
     */
    private String serverAddress;

    /**
     * Name of the user.
     */
    private String username;

    /**
     * Holds the port.
     */
    private int port;

    /**
     * Constructor.
     *
     * @param serverAddress not null.
     * @param username      not null.
     * @param port          not null.
     */
    ClientEntity(@NotNull String serverAddress, @NotNull String username, int port) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.port = port;
    }

    /**
     * Starts the connection to the server
     *
     * @throws ServerNotFoundException If connection gets refused.
     */
    void connect() throws ServerNotFoundException {

        // Connect
        try {
            socket = new Socket(serverAddress, port);
            LOG.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
        } catch (final IOException e) {
            throw new ServerNotFoundException();
        }

        // Create Streams
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOG.error("Error:", e);
            disconnect();
            return;
        }

        // Receives messages from server
        new ServerListener(this).start();

        // Send our username to the server
        try {
            outputStream.writeObject(username);
        } catch (IOException eIO) {
            disconnect();
        }

    }

    /**
     * To send message to serverAddress.
     */
    void sendMessage(@NotNull ChatMessage msg) {
        try {
            outputStream.writeObject(msg);
            LOG.debug(username + " has send a message");
        } catch (IOException e) {
            LOG.error("Error:", e);
        }
    }

    /**
     * Close connection and streams.
     */
    void disconnect() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
        } catch (Exception ignore) {
            // IGNORED
        }
    }

    /**
     * To display in terminal
     */
    void display(@NotNull String message) {
        System.out.println(message);
    }
}
