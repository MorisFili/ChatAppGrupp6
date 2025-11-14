import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your usename: ");
        String username = scanner.nextLine();
        
        Socket socket = new Socket("localhost", 5000);
        NetworkUser networkUser = new NetworkUser(socket);

        ConsoleUI consoleUI = new ConsoleUI(networkUser, username);
        consoleUI.start();
    }
}