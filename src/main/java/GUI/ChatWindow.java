package GUI;

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
import javafx.scene.text.TextFlow;
import model.TextNode;
import session.UserSession;

import java.time.LocalDateTime;

public class ChatWindow {

    private final WindowManager windowManager;
    private final Scene scene;
    private final Button send;
    private final TextArea inputText;
    private final TextFlow mainBody;
    private UserSession user;

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

    public void listenerSetup(){
        // JFX event loop

        // Button click
        send.setOnAction(x -> {
            mainBody.getChildren().add(new TextNode(user.getUsername(), LocalDateTime.now(), inputText.getText()));
            inputText.clear();
        });

        // Text area interceptor för 'enter'
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
    }
}
