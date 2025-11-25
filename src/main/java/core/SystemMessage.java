package core;


import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemMessage extends Message {


    public SystemMessage(String username, String content) {
        super(username, content);
        setFill(Color.DARKGREEN);
        setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 12));
        setText("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "] " +
                "SYSTEM" + ": " + username + " " + content + "\n");
    }

    public String serialize(){
        return "SYS:" + this.username + ":" + this.content;
    }
}
