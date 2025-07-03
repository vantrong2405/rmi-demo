package rmi.chat.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        PUBLIC, SYSTEM
    }

    private String fromUser;
    private String content;
    private Date timestamp;
    private MessageType type;
    private String senderNickname;

    public Message(String fromUser, String content, MessageType type, String senderNickname) {
        this.fromUser = fromUser;
        this.content = content;
        this.type = type;
        this.senderNickname = senderNickname;
        this.timestamp = new Date();
    }

    public String getFromUser() { return fromUser; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public String getSenderNickname() { return senderNickname; }

    public String getFormattedMessage() {
        String time = new SimpleDateFormat("HH:mm:ss").format(timestamp);
        if (type == MessageType.SYSTEM) {
            return String.format("[%s] [HỆ THỐNG] %s", time, content);
        }
        return String.format("[%s] %s: %s", time, senderNickname, content);
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
