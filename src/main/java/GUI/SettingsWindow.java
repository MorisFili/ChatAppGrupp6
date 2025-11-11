package GUI;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import session.UserSession;

public class SettingsWindow {

    private final Button button;
    private final TextField username;
    private UserSession user;
    private final WindowManager windowManager;
    private final Scene scene;

    public SettingsWindow(WindowManager windowManager) {
        this.windowManager = windowManager;
        Label usernameLabel = new Label("Username: ");
        usernameLabel.setFont(new Font(15));
        username = new TextField();
        button = new Button("OK");
        HBox hBox = new HBox(usernameLabel, username, button);
        BorderPane root = new BorderPane();
        root.setCenter(hBox);
        root.setPadding(new Insets(3));

        scene = new Scene(root);

        listenerSetup();
    }

    public void listenerSetup(){
        // JFX event loop

        // Button click
        button.setOnAction(x -> {
            // Parse logic, null checks etc
            user = new UserSession(username.getText());
            windowManager.showChat(user);
        });
    }

    public Scene getScene() {
        return scene;
    }
}
