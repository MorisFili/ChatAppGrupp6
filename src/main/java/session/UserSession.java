package session;

public class UserSession {

    // All runtime-info här

    private final String username;
    private final String group;
    private final int ip;
    private final int port;

    public UserSession(String username, String group, int ip, int port) {
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
