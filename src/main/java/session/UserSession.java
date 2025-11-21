package session;

import model.TextNode;
import network.NetworkUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSession {

    // All runtime-info här
    private final String username;
    private final String group;
    private final String ip;
    private final int targetPort;
    private final int listenerPort;

    private final List<TextNode> chatLog = Collections.synchronizedList(new ArrayList<>());
    private final NetworkUser networkUser;

    public UserSession(String username, String group, String ip, int listenerPort, int targetPort, NetworkUser networkUser) {
        this.username = username;
        this.group = group;
        this.ip = ip;
        this.listenerPort = listenerPort;
        this.targetPort = targetPort;
        this.networkUser = networkUser;
    }

    public NetworkUser getNetworkUser() { return networkUser; }

    public String getUsername() {
        return username;
    }

    public String getGroup() {
        return group;
    }

    public List<TextNode> getChatLog() {
        return chatLog;
    }

    public String getIp() {
        return ip;
    }

    public int getListenerPort() {
        return listenerPort;
    }

    public int getTargetPort() {
        return targetPort;
    }
}
