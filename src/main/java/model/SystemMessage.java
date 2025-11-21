package model;


import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemMessage extends Text {

    private final String username;
    private final String content;

    public SystemMessage(String username, String content) {
        this.username = username;
        this.content = content;

        setFill(Color.DARKGREEN);
        setFont(Font.font("System", FontWeight.BOLD, 10));
        setText("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "] " +
                "SYSTEM" + ": " + username + " " + content + "\n");
    }

    public String serializeMSG(){
        return "SYS:" + username + "|" + content;
    }

    public static SystemMessage deserializeMSG(String msg) {
        String[] p = msg.substring(4).split("\\|");
        return new SystemMessage(p[0], p[1]);
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
