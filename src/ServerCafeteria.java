import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCafeteria {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started 2...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server started...");
                (new ClientHandler(clientSocket)).run();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
