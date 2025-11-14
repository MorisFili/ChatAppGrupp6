
import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI {
    //Handle reading input and DISPLAYING messages

    private final NetworkUser networkUser;
    private final String username;

    public ConsoleUI(NetworkUser networkUser, String username) {
        this.networkUser = networkUser;
        this.username = username;
    }

    public void start() {
        new Thread(() -> {
            try {
                while(networkUser.isConnected()) {
                    String message = networkUser.receiveMessage();
                    if(message == null) break;
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                networkUser.close();
            }
        }).start();

        try(Scanner scanner = new Scanner(System.in)) {
            while(networkUser.isConnected()) {
                String text = scanner.nextLine();
                networkUser.sendMessage(username + ": " + text);
            }
        } catch (IOException e) {
            System.out.println("Error sending message");
        }
    }

    public String getUsername() {
        return username;
    }
}
