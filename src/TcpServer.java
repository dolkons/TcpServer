import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dolkons on 06.02.17.
 */
public class TcpServer extends Thread {
    public static final int SERVERPORT = 8999;
    public static final String CLOSED_CONNECTION = "kazy_closed_connection";
    public static final String LOGIN_NAME = "kazy_login_name";
    // while this is true the server will run
    private boolean running = false;
    // used to send messages
    private PrintWriter bufferSender;
    // callback used to notify new messages received
    private OnMessageReceived messageListener;
    private ServerSocket serverSocket;
    private Socket client;

    /**
     * Constructor of the class
     *
     * @param messageListener listens for the messages
     */
    public TcpServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Close the server
     */
    public void close() {

        running = false;

        if (bufferSender != null) {
            bufferSender.flush();
            bufferSender.close();
            bufferSender = null;
        }

        try {
            client.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("S: Done.");
        serverSocket = null;
        client = null;

    }

    /**
     * Method to send the messages from server to client
     *
     * @param message the message sent by the server
     */
    public void sendMessage(String message) {
        if (bufferSender != null && !bufferSender.checkError()) {
            bufferSender.println(message);
            bufferSender.flush();
        }
    }

    public boolean hasCommand(String message) {
        if (message != null) {
            if (message.contains(CLOSED_CONNECTION)) {
                messageListener.messageReceived(message.replaceAll(CLOSED_CONNECTION, "") + " disconnected from room.");
                // close the server connection if we have this command and rebuild a new one
                close();
                runServer();
                return true;
            } else if (message.contains(LOGIN_NAME)) {
                messageListener.messageReceived(message.replaceAll(LOGIN_NAME, "") + " connected to room.");
                return true;
            }
        }

        return false;
    }

    /**
     * Builds a new server connection
     */
    private void runServer() {
        running = true;

        try {
            System.out.println("S: Connecting...");

            //create a server socket. A server socket waits for requests to come in over the network.
            serverSocket = new ServerSocket(SERVERPORT);

            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            client = serverSocket.accept();

            System.out.println("S: Receiving...");

            try {

                //sends the message to the client
                OutputStream outputStream = client.getOutputStream();
                bufferSender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                sendMessage("Hello!");

                //read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {

                    String message = null;
                    try {
                        message = in.readLine();
                    } catch (IOException e) {
                        System.out.println("Error reading message: " + e.getMessage());
                    }

                    if (hasCommand(message)) {
                        continue;
                    }

                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                System.out.println("S: Error");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        runServer();

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}

