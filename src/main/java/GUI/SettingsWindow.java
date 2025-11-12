package GUI;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import session.UserSession;

public class SettingsWindow {

    private final Button button;
    private final TextField username;
    private final TextField group;
    private final TextField ipAddress;
    private UserSession user;
    private final WindowManager windowManager;
    private final Scene scene;

    public SettingsWindow(WindowManager windowManager) {
        this.windowManager = windowManager;
        Label usernameLabel = new Label("Username:  ");
        Label groupLabel = new Label("Group:        "); // Lazy alignment
        Label ipLabel = new Label("IP Address: ");
        groupLabel.setFont(new Font(15));
        usernameLabel.setFont(new Font(15));
        ipLabel.setFont(new Font(15));
        username = new TextField();
        group = new TextField("default");
        ipAddress = new TextField("0.0.0.0:0000");
        button = new Button("OK");
        HBox hBox = new HBox(usernameLabel, username, button);
        HBox hBoxM = new HBox(groupLabel, group);
        HBox hBoxU = new HBox(ipLabel, ipAddress);
        VBox vBox = new VBox(hBoxU, hBoxM, hBox);
        BorderPane root = new BorderPane();
        root.setCenter(vBox);
        root.setPadding(new Insets(3));

        scene = new Scene(root);

        listenerSetup();
    }

    public void listenerSetup(){
        // JFX event loop

        // Button click
        button.setOnAction(x -> {
            try {
                String[] ipPort = ipAddress.getText().split(":");
                int ip = Integer.parseInt(ipPort[0]);
                int port = Integer.parseInt(ipPort[1]);
                user = new UserSession(username.getText(), group.getText(), ip, port);
                windowManager.showChat(user);
            }catch (NumberFormatException e) {
                System.out.println("Invalid host address format -> " + e.getMessage());
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}
