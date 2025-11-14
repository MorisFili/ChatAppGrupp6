
import java.io.IOException;
import java.net.ServerSocket;

public class ServerController {

    private final Server server;

    public ServerController(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        this.server = new Server(serverSocket);
    }

    public void run() {
        System.out.println("Server has started");
        server.startServer();
    }

    public void stop() {
        System.out.println("Server has stopped");
        server.closeServerSocket();
    }

    public static void main(String[] args) throws IOException {
        ServerController serverController = new ServerController(5000);
        serverController.run();
    }
}
