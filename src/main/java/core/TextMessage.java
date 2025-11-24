package core;

import GUI.ChatWindow;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextMessage extends Message {

    private LocalDateTime timestamp = LocalDateTime.now();

    public TextMessage(String username, String content) {
        super(username, content);

        setFont(Font.font("Segoe UI Emoji", 12));
        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": " + content + "\n");

        // Right-click menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        MenuItem copy = new MenuItem("Copy");
        MenuItem kick = new MenuItem("Kick" + " " + username);

        kick.setOnAction(x -> {
            PrintWriter target = ChatWindow.instance.getNetwork().getPeers().get(username);
            ChatWindow.instance.getNetwork().sendLine(target, "KILL:" + ChatWindow.instance.getUserSession().getUsername());
        });

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

        menu.getItems().addAll(delete, copy, kick);
        setOnMouseClicked(x -> {
            if (x.getButton() == MouseButton.SECONDARY) menu.show(this, x.getScreenX(), x.getScreenY());
        });
    }
  
    public String serialize(){
        return "MSG:" + username + ":" + content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
