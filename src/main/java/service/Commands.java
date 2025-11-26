package service;

import core.Message;
import core.SystemMessage;
import core.TextMessage;
import core.TypingIndicator;
import network.Network;
import service.netCommands.*;
import utils.AutoInject;

import java.util.ArrayList;
import java.util.List;

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
        if (message instanceof TextMessage textMessage) msg.out(textMessage);
        if (message instanceof SystemMessage systemMessage) sys.out(systemMessage);
        if (message instanceof TypingIndicator typingIndicator) typing.out(typingIndicator);
    }


    public List<netCommand> getCommands() {
        return commands;
    }
}
