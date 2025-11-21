package model;

import GUI.ChatWindow;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.PrintWriter;
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

        setFont(Font.font("Segoe UI Emoji", 12));
        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": " + content + "\n");

        // Right-click menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        MenuItem copy = new MenuItem("Copy");
        MenuItem kick = new MenuItem("Kick" + " " + username);

        kick.setOnAction(x -> {
            PrintWriter target = ChatWindow.instance.getNetwork().getPeers().get(username);
            ChatWindow.instance.getNetwork().sendLine(target, "killswitch:" + ChatWindow.instance.getUser().getUsername());
        });

        delete.setOnAction(x -> {
            // Delete logic
            setText("Message has been deleted by: " + ChatWindow.instance.getUser().getUsername() + "\n");
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
  
    public String serializeMSG(){
        return "MSG:" + username + "|" + content;
    }

    public static TextNode deserializeMSG(String msg) {
        String[] p = msg.substring(4).split("\\|");
        return new TextNode(p[0], LocalDateTime.now(), p[1]);
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}
