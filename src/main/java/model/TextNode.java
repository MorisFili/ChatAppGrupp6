package model;

import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextNode extends Text {
    private final String username;
    private final LocalDateTime timestamp;
    private final String content;

    public TextNode(String username, LocalDateTime timestamp, String content) {
        this.content = content;
        this.username = username;
        this.timestamp = timestamp;

        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": " + content + "\n");

    }

    // Lägde getters eftersom jag vill skriva fälterna till databasen
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
