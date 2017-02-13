
public class Main {

    private static TcpServer tcpServer;

    public static void main(String[] args) {
        tcpServer = new TcpServer(new TcpServer.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                System.out.println("=====================");
                System.out.println(message);
                System.out.println("Send to client: " + String.valueOf(Integer.parseInt(message) + 1) );
                tcpServer.sendMessage(String.valueOf(Integer.parseInt(message) + 1));
            }
        });

        tcpServer.run();
    }
}