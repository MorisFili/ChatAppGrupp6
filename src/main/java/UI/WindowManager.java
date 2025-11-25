package UI;

import javafx.stage.Stage;
import network.UserSession;

public class WindowManager {

    private final Stage stage;
    private final ChatWindow chatWindow;
    private final SettingsWindow settingsWindow;

    public WindowManager(Stage stage) {
        this.stage = stage;
        this.chatWindow = new ChatWindow(this);
        this.settingsWindow = new SettingsWindow(this);

        stage.setScene(settingsWindow.getScene());
        stage.setTitle("Settings");
        stage.show();
    }

    public void showChat(UserSession session){
        chatWindow.setUserSession(session);
        stage.setTitle("Chat session - " + session.getUsername());
        stage.setScene(chatWindow.getScene());
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }
}
