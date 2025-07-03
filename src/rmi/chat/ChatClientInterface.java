package rmi.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import rmi.chat.models.Message;

public interface ChatClientInterface extends Remote {

    public void receivePublicMessage(Message message) throws RemoteException;
    public void receivePrivateMessage(Message message) throws RemoteException;
    public void receiveBroadcastMessage(Message message) throws RemoteException;
    public void receiveSystemMessage(String message) throws RemoteException;

    public void notifyUserJoined(String username, String roomName) throws RemoteException;
    public void notifyUserLeft(String username, String roomName) throws RemoteException;
    public void notifyUserOnline(String username) throws RemoteException;
    public void notifyUserOffline(String username) throws RemoteException;

    public boolean ping() throws RemoteException;
}
