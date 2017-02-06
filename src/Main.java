
public class Main {

    private static TcpServer tcpServer;

    public static void main(String[] args) {
        tcpServer = new TcpServer(new TcpServer.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                System.out.println("=====================");
                System.out.println(message);
            }
        });

        tcpServer.run();
    }
}