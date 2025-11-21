package GUI;

import database.IMessageRepository;
import database.MessageRepository;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.TextNode;
import network.NetworkUser;
import session.UserSession;

import java.io.IOException;
import java.time.LocalDateTime;

public class ChatWindow {

    private final WindowManager windowManager;
    private final Scene scene;
    private final Button send;
    private final TextArea inputText;
    private final TextFlow mainBody;
    private UserSession user;
    private final IMessageRepository messageRepository = new MessageRepository();

    public ChatWindow(WindowManager windowManager){
        this.windowManager = windowManager;

        mainBody = new TextFlow();

        ScrollPane scrollPane = new ScrollPane(mainBody);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        inputText = new TextArea();
        inputText.setWrapText(true);
        inputText.setPrefRowCount(1);


        send = new Button(">");
        send.setMinSize(40,60);
        send.setDefaultButton(true);

        HBox bottomAlignment = new HBox(inputText, send);
        HBox.setHgrow(inputText, Priority.ALWAYS);

        VBox root = new VBox(scrollPane, bottomAlignment);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setPadding(new Insets(3));

        scene = new Scene(root,500, 300);

        listenerSetup();
    }

    private void startMessageReceiver(NetworkUser networkUser) {
        new Thread(() -> {
            try {
                while(networkUser.isConnected()) {
                    String message = networkUser.receiveMessage();
                    if(message == null) break;

                    Platform.runLater(() -> {
                        mainBody.getChildren().add(new Text(message + "\n"));
                    });
                }
            } catch (IOException e) {
                System.out.println("Receiver thread error: " + e.getMessage());
            } finally {
                networkUser.close();
            }
        }).start();
    }

    public void listenerSetup(){
        // JFX event loop

        // Button click
        send.setOnAction(x -> {
            String messageContent = inputText.getText();
            if (messageContent.isEmpty()) return;

            // Retrieve the NetworkUser from the session
            NetworkUser networkUser = user.getNetworkUser();
            String username = user.getUsername();

            // Some changes to save the messages in the text file
            try {
                TextNode localMessageNode = new TextNode(username, LocalDateTime.now(), messageContent);
                mainBody.getChildren().add(localMessageNode);
                messageRepository.saveMessage(localMessageNode);
                networkUser.sendMessage(username + ": " + messageContent);
            } catch (IOException e) {
                mainBody.getChildren().add(new Text("Error sending message: Server connection lost.\n"));
                System.out.println("Error sending message: " + e.getMessage());
            }

            inputText.clear();
        });

        inputText.addEventFilter(KeyEvent.KEY_PRESSED, x -> {
            if (x.getCode() == KeyCode.ENTER && !x.isShiftDown()){
                send.fire();
                x.consume();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

    public void setUser(UserSession user) {
        this.user = user;
        startMessageReceiver(user.getNetworkUser());
    }
}
