package rmi.chat.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> userList;

    public ChatRoom(String name) {
        this.name = name;
        this.userList = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<String> getUserList() { return userList; }

    public boolean addUser(String username) {
        if (!userList.contains(username)) {
            userList.add(username);
            return true;
        }
        return false;
    }

    public boolean removeUser(String username) {
        return userList.remove(username);
    }

    @Override
    public String toString() {
        return String.format("%s (%d users)", name, userList.size());
    }
}
