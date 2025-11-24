package service.netCommands;

import core.Message;
import core.SystemMessage;
import javafx.application.Platform;
import network.Network;

public class OK extends netCommand {
    public OK(Network network) {
        super(network, "OK");
    }

    @Override
    public void in(String line) {
        String[] msg = line.split(":");
        String username = msg[1];
        String content = "has connected.";
        SystemMessage message = new SystemMessage(username, content);
        Platform.runLater(() -> network.getChatWindow().getMainBody().getChildren().add(message));
    }

    @Override
    public void out(Message msg) {
        super.out(msg);
    }
}
