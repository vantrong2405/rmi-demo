package rmi.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import rmi.chat.models.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface {
    private static final long serialVersionUID = 1L;
    private ChatServerInterface server;
    private String username;
    private String nickname;
    private boolean isConnected;
    private volatile boolean shouldExit = false;

    // GUI Components
    private JFrame frame;
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel statusLabel;
    private StyledDocument doc;
    private Style defaultStyle;
    private Style boldStyle;

    public ChatClient() throws RemoteException {
        super();
        this.isConnected = false;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        frame = new JFrame("Phòng Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(0, 0));

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(52, 152, 219)); // Nice blue color
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        statusLabel = new JLabel("Đang kết nối tới server...");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        frame.add(statusPanel, BorderLayout.NORTH);

        // Chat Area with styles
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        chatArea.setBackground(Color.WHITE);

        // Create styles
        doc = chatArea.getStyledDocument();
        defaultStyle = chatArea.addStyle("default", null);
        StyleConstants.setFontFamily(defaultStyle, "SansSerif");
        StyleConstants.setFontSize(defaultStyle, 14);

        boldStyle = chatArea.addStyle("bold", defaultStyle);
        StyleConstants.setBold(boldStyle, true);
        StyleConstants.setForeground(boldStyle, new Color(41, 128, 185)); // Nice blue for own messages

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);

        // Message Panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        messagePanel.setBackground(new Color(236, 240, 241)); // Light gray background

        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        sendButton = new JButton("Gửi");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setBackground(new Color(52, 152, 219));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setOpaque(true);
        sendButton.setContentAreaFilled(true);
        sendButton.setBorderPainted(true);

        // Ensure button uses system look and feel
        try {
            sendButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        } catch (Exception e) {}

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        frame.add(messagePanel, BorderLayout.SOUTH);

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        // Window listener for cleanup
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });

        // Custom message display
        chatArea.setBackground(new Color(245, 247, 249));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        messageField.requestFocus();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && isConnected) {
            try {
                server.sendPublicMessage(username, message);
                messageField.setText("");
            } catch (Exception e) {
                appendToChatArea("Lỗi gửi tin nhắn: " + e.getMessage());
            }
        }
        messageField.requestFocus();
    }

    public void start() {
        if (connectToServer() && login()) {
            startListening();
        } else {
            frame.dispose();
        }
    }

    private boolean connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ChatServerInterface) registry.lookup("ChatService");
            statusLabel.setText("Đã kết nối tới server");
            isConnected = true;
            return true;
        } catch (Exception e) {
            statusLabel.setText("Kết nối thất bại!");
            appendToChatArea("Lỗi: Không thể kết nối tới server");
            appendToChatArea("Hãy đảm bảo server đang chạy!");
            return false;
        }
    }

    private boolean login() {
        try {
            // Custom login dialog
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel label = new JLabel("Nhập tên của bạn:");
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            panel.add(label, BorderLayout.NORTH);

            JTextField field = new JTextField();
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            panel.add(field, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Đăng nhập",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String input = field.getText().trim();
                if (input.isEmpty()) {
                    return false;
                }

                username = input;
                nickname = username;

                if (server.login(username, nickname, this)) {
                    frame.setTitle("Phòng Chat - " + nickname);
                    statusLabel.setText("Đang chat với tên: " + nickname);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(frame,
                        "Đăng nhập thất bại! Tên này đã có người dùng.",
                        "Lỗi Đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            appendToChatArea("Lỗi đăng nhập: " + e.getMessage());
            return false;
        }
    }

    private void startListening() {
        Thread messageThread = new Thread(this::keepConnectionAlive);
        messageThread.setDaemon(true);
        messageThread.start();
    }

    @Override
    public void receivePublicMessage(Message message) throws RemoteException {
        String formattedMessage = String.format("%s: %s",
            message.getSenderNickname(),
            message.getContent());
        appendToChatArea(formattedMessage);
    }

    @Override
    public void receivePrivateMessage(Message message) throws RemoteException {
        appendToChatArea("[RIÊNG TƯ] " + message.getFormattedMessage());
    }

    @Override
    public void receiveBroadcastMessage(Message message) throws RemoteException {
        appendToChatArea("[THÔNG BÁO] " + message.getFormattedMessage());
    }

    @Override
    public void notifyUserJoined(String username, String roomName) throws RemoteException {
        appendToChatArea(username + " đã tham gia chat");
    }

    @Override
    public void notifyUserLeft(String username, String roomName) throws RemoteException {
        appendToChatArea(username + " đã rời phòng chat");
    }

    @Override
    public void notifyUserOnline(String username) throws RemoteException {
        appendToChatArea(username + " đã online");
    }

    @Override
    public void notifyUserOffline(String username) throws RemoteException {
        appendToChatArea(username + " đã offline");
    }

    @Override
    public void receiveSystemMessage(String message) throws RemoteException {
        appendToChatArea("[HỆ THỐNG] " + message);
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    private void keepConnectionAlive() {
        while (isConnected && !shouldExit) {
            try {
                if (server != null) {
                    server.getUserCount();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                if (!shouldExit) {
                    statusLabel.setText("Disconnected from server!");
                    isConnected = false;
                }
                break;
            }
        }
    }

    private void cleanup() {
        try {
            shouldExit = true;
            isConnected = false;
            if (server != null) {
                server.logout(username);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ChatClient client = new ChatClient();
                client.start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Lỗi khởi động client: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void appendToChatArea(String formattedMessage) {
        SwingUtilities.invokeLater(() -> {
            try {
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String fullMessage = String.format("[%s] %s%n", timestamp, formattedMessage);
                doc.insertString(doc.getLength(), fullMessage, defaultStyle);
                chatArea.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                System.err.println("Error appending message: " + e.getMessage());
            }
        });
    }
}
