package GUI;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private final TextField targetPort;
    private final TextField listenerPort;
    private UserSession user;
    private final WindowManager windowManager;
    private final Scene scene;

    public SettingsWindow(WindowManager windowManager) {
        this.windowManager = windowManager;
        Label usernameLabel = new Label("Username:     ");
        Label groupLabel = new Label("Group:           "); // Lazy alignment
        Label ipLabel = new Label("IP Address:    ");
        Label listenerLabel = new Label("Listener port: ");
        Label portLabel = new Label("Port: ");
        groupLabel.setFont(new Font(15));
        usernameLabel.setFont(new Font(15));
        ipLabel.setFont(new Font(15));
        listenerLabel.setFont(new Font(15));
        portLabel.setFont(new Font(15));
        username = new TextField();
        group = new TextField("default");
        ipAddress = new TextField("0.0.0.0");
        listenerPort = new TextField("5050");
        targetPort = new TextField("5050");
        targetPort.setMaxSize(50,10);
        button = new Button("Connect");
        button.setMinSize(75,10);
        HBox hBox = new HBox(usernameLabel, username, button);
        HBox.setMargin(username, new Insets(0,10,0,0));
        HBox hBoxM = new HBox(groupLabel, group);
        HBox hBoxU = new HBox(ipLabel, ipAddress, portLabel, targetPort);
        HBox.setMargin(ipAddress, new Insets(0,5,0,0));
        HBox hBoxUU = new HBox(listenerLabel, listenerPort);
        VBox vBox = new VBox(hBoxUU, hBoxU, hBoxM, hBox);
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
            if (username.getText().isEmpty()) {
                new Alert(Alert.AlertType.NONE, "Username missing.", ButtonType.OK).showAndWait();
            } else {
                try {
                    user = new UserSession(username.getText(), group.getText(), ipAddress.getText(),
                            Integer.parseInt(listenerPort.getText()), Integer.parseInt(targetPort.getText()));
                    windowManager.showChat(user);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid host address format -> " + e.getMessage());
                }
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}
