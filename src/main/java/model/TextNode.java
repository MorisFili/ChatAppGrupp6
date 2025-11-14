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

    public String serializeMSG(){
        return "MSG:" + username + "|" + content;
    }

    public static TextNode deserializeMSG(String msg) {
        String[] p = msg.substring(4).split("\\|");
        return new TextNode(p[0], LocalDateTime.now(), p[1]);
    }
}
