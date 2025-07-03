package rmi.chat.models;

import java.io.Serializable;
import rmi.chat.ChatClientInterface;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String nickname;
    private transient ChatClientInterface clientCallback;
    
    public User(String username, String nickname, ChatClientInterface clientCallback) {
        this.username = username;
        this.nickname = nickname;
        this.clientCallback = clientCallback;
    }
    
    public String getUsername() { return username; }
    public String getNickname() { return nickname; }
    public ChatClientInterface getClientCallback() { return clientCallback; }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", nickname, username);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
} 
