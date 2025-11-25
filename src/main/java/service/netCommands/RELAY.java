package service.netCommands;

import network.Network;

import java.io.PrintWriter;
import java.net.Socket;

public class RELAY extends netCommand {
    public RELAY(Network network) {
        super(network, "RELAY");
    }

    @Override
    public void in(String line) {
        String[] args = line.split(":", 3);
        network.connect(args[1], Integer.parseInt(args[2]));

    }

    public void out(String ip, int port) {
        String line = "RELAY:" + ip + ":" + port;
        for (PrintWriter pw : network.getPeers().values()) {
            pw.println(line);
        }
    }
}
