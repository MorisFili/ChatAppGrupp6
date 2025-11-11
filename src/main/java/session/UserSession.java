package session;

public class UserSession {

    // All runtime-info här

    private final String username;

    public UserSession(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
