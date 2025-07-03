package rmi.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import rmi.chat.models.*;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
    private static final String SERVER_NAME = "ChatMaster-Server";
    private static final String DATA_DIR = "data";
    private Date startTime;

    private java.util.Map<String, User> onlineUsers;
    private ChatRoom chatRoom;
    private java.util.List<Message> messageHistory;

    private JFrame frame;
    private JLabel statusLabel;

    public ChatServer() throws RemoteException {
        super();
        this.startTime = new Date();
        this.onlineUsers = new ConcurrentHashMap<>();
        this.messageHistory = new ArrayList<>();
        this.chatRoom = new ChatRoom("Chung");
        createAndShowGUI();
        new File(DATA_DIR).mkdirs();
        logToUI("Khởi tạo server thành công!");
    }

    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        frame = new JFrame("Màn hình Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);
        frame.setLayout(new BorderLayout(5, 5));

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Đang khởi động server...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel addressLabel = new JLabel("Địa chỉ: localhost:1099");
        addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        statusPanel.add(statusLabel);
        statusPanel.add(addressLabel);

        frame.add(statusPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void logToUI(String message) {
        if (message.contains("khởi tạo") ||
            message.contains("online") ||
            message.contains("kết nối")) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(message);
            });
        }
        System.out.println(message);
    }

    private void updateStatus() {
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(String.format("Số người online: %d | Tin nhắn: %d",
                    onlineUsers.size(), messageHistory.size()));
            });
        }
    }

    @Override
    public synchronized boolean login(String username, String nickname, ChatClientInterface clientCallback) throws RemoteException {
        try {
            if (onlineUsers.containsKey(username)) {
                logToUI("Đăng nhập thất bại: " + username + " đã online");
                return false;
            }

            User newUser = new User(username, nickname, clientCallback);
            onlineUsers.put(username, newUser);
            chatRoom.addUser(username);

            notifyAllUsers("notifyUserOnline", username);

            Message systemMsg = new Message("HỆ THỐNG",
                nickname + " đã tham gia chat!",
                Message.MessageType.SYSTEM,
                "HỆ THỐNG");
            broadcastMessage(systemMsg);

            logToUI("Người dùng đăng nhập: " + nickname + " (" + username + ")");
            updateStatus();
            saveMessageHistory();
            return true;

        } catch (Exception e) {
            logToUI("Lỗi đăng nhập cho " + username + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized boolean logout(String username) throws RemoteException {
        try {
            User user = onlineUsers.get(username);
            if (user == null) return false;

            String nickname = user.getNickname();
            chatRoom.removeUser(username);
            onlineUsers.remove(username);

            notifyAllUsers("notifyUserOffline", username);

            Message systemMsg = new Message("HỆ THỐNG",
                nickname + " đã rời phòng chat!",
                Message.MessageType.SYSTEM,
                "HỆ THỐNG");
            broadcastMessage(systemMsg);

            logToUI("Người dùng đăng xuất: " + nickname + " (" + username + ")");
            updateStatus();
            saveMessageHistory();
            return true;

        } catch (Exception e) {
            logToUI("Lỗi đăng xuất cho " + username + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public java.util.List<User> getOnlineUsers() throws RemoteException {
        return new ArrayList<>(onlineUsers.values());
    }

    @Override
    public boolean isUserOnline(String username) throws RemoteException {
        return onlineUsers.containsKey(username);
    }

    @Override
    public int getUserCount() throws RemoteException {
        return onlineUsers.size();
    }

    @Override
    public synchronized boolean sendPublicMessage(String username, String message) throws RemoteException {
        try {
            User sender = onlineUsers.get(username);
            if (sender == null) return false;

            Message msg = new Message(username, message, Message.MessageType.PUBLIC, sender.getNickname());
            messageHistory.add(msg);
            broadcastMessage(msg);

            logToUI("[Chung] " + sender.getNickname() + ": " + message);
            updateStatus();
            saveMessageHistory();
            return true;

        } catch (Exception e) {
            logToUI("Lỗi gửi tin nhắn: " + e.getMessage());
            return false;
        }
    }

    private void broadcastMessage(Message message) {
        for (User user : onlineUsers.values()) {
            if (user.getClientCallback() != null) {
                try {
                    if (message.getType() == Message.MessageType.SYSTEM) {
                        user.getClientCallback().receiveSystemMessage(message.getContent());
                    } else {
                        user.getClientCallback().receivePublicMessage(message);
                    }
                } catch (RemoteException e) {
                    logToUI("Không thể gửi tin nhắn tới " + user.getUsername());
                }
            }
        }
    }

    private void notifyAllUsers(String method, String username) {
        for (User user : onlineUsers.values()) {
            if (!user.getUsername().equals(username) && user.getClientCallback() != null) {
                try {
                    switch (method) {
                        case "notifyUserOnline":
                            user.getClientCallback().notifyUserOnline(username);
                            break;
                        case "notifyUserOffline":
                            user.getClientCallback().notifyUserOffline(username);
                            break;
                    }
                } catch (RemoteException e) {
                    logToUI("Không thể thông báo tới " + user.getUsername());
                }
            }
        }
    }

    private void saveMessageHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "/message_history.dat"))) {
            oos.writeObject(messageHistory);
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ChatServer server = new ChatServer();
            registry.bind("ChatService", server);
            server.statusLabel.setText("Server đang chạy | Chưa có người dùng kết nối");
        } catch (Exception e) {
            System.err.println("Khởi động server thất bại: " + e.toString());
            e.printStackTrace();
        }
    }
}
