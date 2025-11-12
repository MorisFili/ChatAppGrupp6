package session;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserSession {

    // All runtime-info här

    private final String username;
    private final String group;
    private final String ip;
    private final int port;
    private final List<PrintWriter> peers = Collections.synchronizedList(new ArrayList<>()); // Lista av peers för output stream, här eller annanstans

    public UserSession(String username, String group, String ip, int port) {
        this.username = username;
        this.group = group;
        this.ip = ip;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getGroup() {
        return group;
    }
}
