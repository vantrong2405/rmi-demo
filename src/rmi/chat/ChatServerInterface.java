package rmi.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import rmi.chat.models.User;

public interface ChatServerInterface extends Remote {

    // User authentication and management
    public boolean login(String username, String nickname, ChatClientInterface clientCallback) throws RemoteException;
    public boolean logout(String username) throws RemoteException;
    public List<User> getOnlineUsers() throws RemoteException;
    public boolean isUserOnline(String username) throws RemoteException;
    public int getUserCount() throws RemoteException;

    // Messaging
    public boolean sendPublicMessage(String username, String message) throws RemoteException;
}
