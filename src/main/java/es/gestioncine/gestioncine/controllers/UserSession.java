package es.gestioncine.gestioncine.controllers;

public class UserSession {
    private static UserSession instance;
    private String username;
    private String email;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void clearSession() {
        username = null;
        email = null;
    }
}
