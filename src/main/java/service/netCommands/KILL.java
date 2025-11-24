package service.netCommands;

import core.Message;
import core.SystemMessage;
import network.Network;

import java.io.PrintWriter;

public class KILL extends netCommand {

    public KILL(Network network) {
        super(network, "KILL");
    }

    @Override
    public void in(String line) {
        String[] args = line.split(":");
        String username = network.getUserSession().getUsername();
        String content = (" was kicked by " + args[1]);
        SystemMessage systemMessage = new SystemMessage(username, content);
        out(systemMessage);
        network.terminateNetwork();
    }

    @Override
    public void out(Message msg) {
        for (PrintWriter pw : network.getPeers().values()) {
            pw.println(msg.serialize());
        }
    }
}
