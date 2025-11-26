package core;

import java.time.format.DateTimeFormatter;

public class TypingIndicator extends Message {

    private final boolean typing;

    public TypingIndicator(String username, Boolean typing) {
        super(username, "is typing...");
        this.typing = typing;

        setStyle("-fx-font-style: italic; -fx-opacity: 0.6;");
        setText(username + ": " + content + "\n");
    }

    @Override
    public String serialize() {
        return "TYPING:" + username + ":" + typing;
    }
}
