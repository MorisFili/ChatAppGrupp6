package core;

import javafx.scene.text.Text;

public abstract class Message extends Text {
    protected final String username;
    protected final String content;



    public Message(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String serialize(){
        return "";
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
