package service.netCommands;

import core.Message;
import javafx.application.Platform;
import core.SystemMessage;
import network.Network;

import java.io.IOException;
import java.io.PrintWriter;

public class SYS extends netCommand {
    public SYS(Network network) {
        super(network, "SYS");
    }

    @Override
    public void in(String line) {
        String[] msg = line.split(":");
        String username = msg[1];
        String content = msg[2];
        SystemMessage sys = new SystemMessage(username, content);

        Platform.runLater(() -> network.getChatWindow().getMainBody().getChildren().add(sys));

        if (sys.getContent().contains("disconnected")) {
            try {
                network.getConnections().get(sys.getUsername()).close();
            } catch (IOException _) {
                // ignore
            }
            network.getPeers().get(sys.getUsername()).close();
            network.getConnections().remove(sys.getUsername());
            network.getPeers().remove(sys.getUsername());
        }
    }

    @Override
    public void out(Message msg) {
        for (PrintWriter pw : network.getPeers().values()) {
            pw.println(msg.serialize());
        }
    }
}
