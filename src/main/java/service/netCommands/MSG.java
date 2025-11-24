package service.netCommands;

import core.Message;
import javafx.application.Platform;
import core.TextMessage;
import network.Network;

import java.io.PrintWriter;

public class MSG extends netCommand {

    public MSG(Network network) {
        super(network, "MSG");
    }

    @Override
    public void in(String line) {
        String[] msg = line.split(":");
        String username = msg[1];
        String content = msg[2];

        TextMessage message = new TextMessage(username, content);

        network.getUserSession().getChatLog().add(message);
        network.getChatWindow().getRepository().saveMessage(message);
        Platform.runLater(() -> network.getChatWindow().getMainBody().getChildren().add(message));
    }

    @Override
    public void out(Message msg) {
        network.getUserSession().getChatLog().add((TextMessage) msg);
        network.getChatWindow().getRepository().saveMessage((TextMessage) msg);
        for (PrintWriter pw : network.getPeers().values()) {
            pw.println(msg.serialize());
        }
    }
}
