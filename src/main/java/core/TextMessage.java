package core;

import UI.ChatWindow;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextMessage extends Message {

    private LocalDateTime timestamp = LocalDateTime.now();

    public TextMessage(String username, String content) {
        this(username, content, LocalDateTime.now());

        setFont(Font.font("Segoe UI Emoji", 12));
        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": " + content + "\n");

        // Right-click menu
        ContextMenu menu = new ContextMenu();
        MenuItem pm = new MenuItem("PM");
        MenuItem delete = new MenuItem("Delete");
        MenuItem copy = new MenuItem("Copy");
        MenuItem kick = new MenuItem("Kick" + " " + username);

        pm.setOnAction(x -> ChatWindow.instance.getInputText().setText("@" + username + " "));

        kick.setOnAction(x -> ChatWindow.instance.getNetwork().sendLine(username, "KILL:" + ChatWindow.instance.getUserSession().getUsername()));

        delete.setOnAction(x -> {
            // Delete logic
            setText("Message has been deleted by: " + ChatWindow.instance.getUserSession().getUsername() + "\n");
            ChatWindow.instance.getRepository().deleteMessage(this);
        });

        copy.setOnAction(x -> {
            ClipboardContent c = new ClipboardContent();
            c.putString(content);
            Clipboard.getSystemClipboard().setContent(c);

        });

        menu.getItems().addAll(pm, delete, copy, kick);
        setOnMouseClicked(x -> {
            if (x.getButton() == MouseButton.SECONDARY) menu.show(this, x.getScreenX(), x.getScreenY());
        });
    }

    public TextMessage(String username, String content, LocalDateTime timestamp) {
        super(username, content);
        this.timestamp = timestamp;

        setFont(Font.font("Segoe UI Emoji", 12));
        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": " + content + "\n");
    }

    public static String serializeForFile(TextMessage message) {
        return message.getUsername() + "|" + message.getTimestamp().toString() + "|" + message.getContent();
    }

    public static TextMessage deserializeFromFile(String decryptedMessage) {
        String[] parts = decryptedMessage.split("\\|", 3);

        if (parts.length == 3) {
            try {
                String username = parts[0];

                LocalDateTime timestamp = LocalDateTime.parse(parts[1]);

                String content = parts[2];

                return new TextMessage(username, content, timestamp);
            } catch (Exception e) {
                System.err.println("Failed to parse message content: " + decryptedMessage);
                return null;
            }
        }
        return null;
    }

    public String serialize(){
        return "MSG:" + username + ":" + content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
