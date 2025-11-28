package service;

import java.util.ArrayList;
import java.util.List;

import core.ImageMessage;
import core.Message;
import core.SystemMessage;
import core.TextMessage;
import core.TypingIndicator;
import network.Network;
import service.netCommands.KILL;
import service.netCommands.MSG;
import service.netCommands.RELAY;
import service.netCommands.SYS;
import service.netCommands.TYPING;
import service.netCommands.netCommand;
import utils.AutoInject;

public class Commands {

    List<netCommand> commands = new ArrayList<>();

    @Command KILL kill;
    @Command MSG msg;
    @Command public RELAY relay;
    @Command SYS sys;
    @Command TYPING typing;

    public Commands(Network network){
        AutoInject autoInject = new AutoInject();
        autoInject.register(this, network);

    }

    public void inbound(String line){
        String[] args = line.trim().split(":");

        switch (args[0]){
            case "KILL" -> kill.in(line);
            case "SYS" -> sys.in(line);
            case "MSG" -> msg.in(line);
            case "RELAY" -> relay.in(line);
            case "TYPING" -> typing.in(line);
        }
    }

    public void outbound(Message message){
        if (message instanceof TextMessage || message instanceof ImageMessage) {
            msg.out(message);
        } else if (message instanceof SystemMessage systemMessage) {
            sys.out(systemMessage);
        } else if (message instanceof TypingIndicator typingIndicator) {
            typing.out(typingIndicator);
        }
    }


    public List<netCommand> getCommands() {
        return commands;
    }
}
