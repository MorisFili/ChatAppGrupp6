package GUI;

import javafx.stage.Stage;
import session.UserSession;

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
        chatWindow.setUser(session);
        stage.setTitle("Chat session - " + session.getUsername());
        stage.setScene(chatWindow.getScene());
    }

    public void terminatePool() {
        if (chatWindow.getNetwork() == null) return;
        if (chatWindow.getNetwork().threadPool != null) {
            chatWindow.getNetwork().threadPool.shutdown();
        }
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }
}
