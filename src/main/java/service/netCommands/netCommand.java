package service.netCommands;

import core.Message;
import network.Network;

public abstract class netCommand {
    final Network network;
    private final String cmd;

    public netCommand(Network network, String cmd) {
        this.network = network;
        this.cmd = cmd;
    }

    public void in(String line){}
    public void out(Message msg){}

    public String getCmd() {
        return cmd;
    }
}
