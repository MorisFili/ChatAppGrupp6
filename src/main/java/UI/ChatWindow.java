package UI;

import core.Message;
import core.TypingIndicator;
import database.IMessageRepository;
import database.MessageRepository;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.TextFlow;
import core.TextMessage;
import network.Network;
import network.UserSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ChatWindow {

    private final WindowManager windowManager;
    private final PopupWindow popupWindow;
    public static ChatWindow instance; // Singleton instance
    private final Scene scene;
    private final Button send;
    private final TextArea inputText;
    private final TextFlow mainBody;
    private UserSession userSession;
    private Network network;
    private IMessageRepository repository;

    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private long lastTyped = 0;
    private boolean lastStateSent = false;
    private boolean inputEmpty = true;
    Map<String, TypingIndicator> indicators = new HashMap<>();

    AudioClip messageSound = new AudioClip(getClass().getResource("/notification.mp3").toExternalForm());


    public ChatWindow(WindowManager windowManager) {
        this.windowManager = windowManager;
        instance = this;
        popupWindow = new PopupWindow();

        mainBody = new TextFlow();
        mainBody.setPadding(new Insets(5, 0, 5, 3));
        ScrollPane scrollPane = new ScrollPane(mainBody);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        inputText = new TextArea();
        inputText.setWrapText(true);
        inputText.setPrefRowCount(1);


        send = new Button(">");
        send.setMinSize(40, 60);
        send.setDefaultButton(true);

        HBox bottomAlignment = new HBox(inputText, send);
        HBox.setHgrow(inputText, Priority.ALWAYS);

        VBox root = new VBox(scrollPane, bottomAlignment);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setPadding(new Insets(3));

        scene = new Scene(root, 500, 300);

        listenerSetup();
    }

    public void listenerSetup() {

        // Send
        send.setOnAction(x -> {
            if (inputText.getText().isEmpty()){
                x.consume();
                return;
            }
            String username = userSession.getUsername();
            String content = inputText.getText();
            TextMessage msg = new TextMessage(username, content);
            mainBody.getChildren().add(msg);

            if (content.startsWith("@")) {
                String receiver = content.substring(1).split(" ")[0];
                network.sendLine(receiver, msg.serialize());
            } else network.send(msg);

            inputText.clear();
        });

        // Text area interceptor för 'enter'
        inputText.addEventFilter(KeyEvent.KEY_PRESSED, x -> {
            if (x.getCode() == KeyCode.ENTER && !x.isShiftDown()) {
                if (inputText.getText().isEmpty()) {
                    x.consume();
                    return;
                }
                send.fire();
                x.consume();
            }
        });

        // Incoming text listener
        mainBody.getChildren().addListener((ListChangeListener<Node>) newMsg -> {
            while (newMsg.next()) {
                if (newMsg.wasAdded()) {
                    for (Node n : newMsg.getAddedSubList()) {
                        if (n instanceof TypingIndicator) continue;
                        messageSound.play();
                        if (!windowManager.getStage().isFocused() || windowManager.getStage().isIconified()){
                            if (n instanceof TextMessage textMessage) {
                                popupWindow.showPopup(textMessage, windowManager.getStage());
                            }
                        }
                    }
                }
            }
        });

        // Typing indicator
        inputText.textProperty().addListener((obs, old, newV) -> {
            inputEmpty = newV.isEmpty();
            lastTyped = System.currentTimeMillis();
        });
        timer.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            boolean typing = !inputEmpty && (now - lastTyped < 2000);

            if (typing && !lastStateSent){
                lastStateSent = true;
                network.send(new TypingIndicator(userSession.getUsername(), true));
            }

            if (!typing && lastStateSent){
                lastStateSent = false;
                network.send(new TypingIndicator(userSession.getUsername(), false));
            }

        }, 0, 250, TimeUnit.MILLISECONDS);

    }

    public void loadAndDisplayMessages() {
        // 1. Load messages from file
        List<TextMessage> savedMessages = repository.loadMessages();

        // 2. Display messages in GUI and add to chat log
        for (TextMessage msg : savedMessages) {
            mainBody.getChildren().add(msg);
            userSession.getChatLog().add(msg); // Add to the runtime log
        }
    }

    public void wireNetwork(Network network) {
        this.network = network;
    }

    public Scene getScene() {
        return scene;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
        initRepo();
        loadAndDisplayMessages();
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public TextFlow getMainBody() {
        return mainBody;
    }

    public Network getNetwork() {
        return network;
    }

    public IMessageRepository getRepository() {
        return repository;
    }

    public void initRepo() {
        this.repository = new MessageRepository(userSession.getGroup());
    }

    public TextArea getInputText() {
        return inputText;
    }

    public Map<String, TypingIndicator> getIndicators() {
        return indicators;
    }

    public ScheduledExecutorService getTimer() {
        return timer;
    }
}
