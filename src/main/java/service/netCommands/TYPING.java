package service.netCommands;

import UI.ChatWindow;
import core.Message;
import core.TypingIndicator;
import javafx.application.Platform;
import network.Network;

import java.io.PrintWriter;

public class TYPING extends netCommand {

    public TYPING(Network network) {
        super(network, "TYPING");
    }

    @Override
    public void in(String line) {
        String[] args = line.split(":", 3);
        String username = args[1];
        boolean typing = args[2].trim().equals("true");

        TypingIndicator indicator = new TypingIndicator(username, typing);

        if (typing) {
            if (!ChatWindow.instance.getIndicators().containsKey(username)) {
                ChatWindow.instance.getIndicators().put(username, indicator);
                Platform.runLater(() -> ChatWindow.instance.getMainBody().getChildren().add(indicator));
            }
        } else {
            Platform.runLater(() -> {
                ChatWindow.instance.getMainBody().getChildren().
                        remove(ChatWindow.instance.getIndicators().get(username));
                ChatWindow.instance.getIndicators().remove(username);
            });
        }
    }

    @Override
    public void out(Message msg) {
        for (PrintWriter pw : network.getPeers().values()) {
            pw.println(msg.serialize());
        }
    }
}
