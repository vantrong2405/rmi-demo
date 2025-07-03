# RMI Chat System Demo Application

**Tác giả**: Long
**Môn học**: Hệ phân tán - CS420SA
**Đề tài**: Kỹ thuật RMI (Remote Method Invocation)
**Demo**: Real-time Online Chat System

## 📋 Tổng quan

Đây là ứng dụng demo minh họa kỹ thuật RMI trong Java thông qua một hệ thống chat real-time hoàn chỉnh. Hệ thống bao gồm:

- **🖥️ Chat Server**: Central server quản lý users, rooms, messages
- **💬 Chat Client**: Interactive client với real-time messaging
- **🏠 Multiple Rooms**: General, Tech, Random, VIP chat rooms
- **📨 Private Messaging**: 1-1 private conversations
- **💾 Persistent Storage**: Message history lưu trữ vĩnh viễn
- **🔄 Real-time Callbacks**: Instant message delivery via RMI

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────┐    RMI Calls     ┌─────────────────┐
│   Chat Client   │ ◄──────────────► │   Chat Server   │
│                 │   (Port 1099)    │                 │
│ • ChatClient    │                  │ • ChatServer    │
│   Interface     │                  │   Interface     │
│ • GUI Menu      │                  │ • User Manager  │
│ • Real-time     │                  │ • Room Manager  │
│   Callbacks     │                  │ • Message Queue │
└─────────────────┘                  └─────────────────┘
                                               │
                                               ▼
                                      ┌─────────────────┐
                                      │   Data Storage  │
                                      │                 │
                                      │ • message_      │
                                      │   history.dat   │
                                      │ • Serialized    │
                                      │   Objects       │
                                      └─────────────────┘
```

## 📁 Cấu trúc project

```
rmi/
├── src/rmi/
│   ├── ChatLauncher.java                 # Main launcher
│   └── chat/
│       ├── ChatServerInterface.java      # Server remote interface
│       ├── ChatClientInterface.java      # Client callback interface
│       ├── ChatServer.java               # Server implementation
│       ├── ChatClient.java               # Client implementation
│       └── models/
│           ├── User.java                 # User model
│           ├── ChatRoom.java             # Chat room model
│           └── Message.java              # Message model
├── compile.bat                           # Compile script
├── run_server.bat                        # Server startup script
├── run_client.bat                        # Client startup script
├── data/                                 # Message storage directory
│   └── message_history.dat               # Persistent message history
└── README.md                             # This documentation
```

## 🚀 Cách chạy ứng dụng

### Cách 1: Sử dụng Main Launcher (Khuyến nghị)

1. **Compile toàn bộ project**:
   ```bash
   compile.bat
   ```

2. **Chạy launcher**:
   ```bash
   java rmi.ChatLauncher
   ```

3. **Chọn các options**:
   - Option 1: Start Chat Server (chạy trước)
   - Option 2: Start Chat Client (chạy sau server)
   - Option 3: Multi-Client Demo instructions

### Cách 2: Sử dụng scripts riêng biệt

1. **Compile**:
   ```bash
   compile.bat
   ```

2. **Start Server** (Terminal 1):
   ```bash
   run_server.bat
   ```

3. **Start Client** (Terminal 2):
   ```bash
   run_client.bat
   ```

4. **Start thêm Clients** (Terminal 3, 4, ...):
   ```bash
   run_client.bat
   ```

### Cách 3: Chạy trực tiếp

1. **Compile**:
   ```bash
   javac -d . src/rmi/chat/models/*.java
   javac -d . -cp . src/rmi/chat/*.java
   javac -d . -cp . src/rmi/*.java
   ```

2. **Start Server**:
   ```bash
   java rmi.chat.ChatServer
   ```

3. **Start Client**:
   ```bash
   java rmi.chat.ChatClient
   ```

## 💡 Tính năng chính

### 🖥️ Server Features:
- ✅ **Multi-user management**: Concurrent user handling
- ✅ **4 default chat rooms**: General, Tech, Random, VIP
- ✅ **Real-time messaging**: Instant message delivery
- ✅ **Private messaging**: 1-1 secure conversations
- ✅ **User authentication**: Username/nickname system
- ✅ **Message persistence**: Auto-save to disk
- ✅ **Broadcast messaging**: Admin announcements
- ✅ **Connection monitoring**: Auto-cleanup dead connections
- ✅ **Room management**: Join/leave notifications
- ✅ **Message history**: Retrieve past conversations

### 💬 Client Features:
- ✅ **Interactive menu**: User-friendly CLI interface
- ✅ **Real-time callbacks**: Instant message reception
- ✅ **Multi-room support**: Switch between chat rooms
- ✅ **Private messaging**: Send/receive private messages
- ✅ **Online user list**: See who's currently online
- ✅ **Message history**: View room and private history
- ✅ **Broadcast reception**: Receive server announcements
- ✅ **Status notifications**: User join/leave/online/offline
- ✅ **Connection keepalive**: Maintain server connection
- ✅ **Graceful logout**: Clean disconnect from server

## 🔧 Cách thức hoạt động RMI

### 1. **Server Initialization**:
```java
// Server creates RMI Registry and binds service
Registry registry = LocateRegistry.createRegistry(1099);
registry.bind("ChatService", server);
```

### 2. **Client Connection**:
```java
// Client connects and gets server reference
Registry registry = LocateRegistry.getRegistry("localhost", 1099);
server = (ChatServerInterface) registry.lookup("ChatService");
```

### 3. **Bidirectional Communication**:
```java
// Client calls server methods
server.sendPublicMessage(username, roomName, message);

// Server calls back to client
client.receivePublicMessage(message);
```

### 4. **Object Serialization**:
```java
// Objects automatically serialized over network
Message message = new Message(from, to, content, type);
server.sendPrivateMessage(fromUser, toUser, message);
```

## 📊 Demo scenarios

### Scenario 1: Multi-room Chat
1. **User A** joins "General" room
2. **User B** joins "Tech" room
3. Both users chat in their respective rooms
4. **User A** switches to "Tech" room
5. Both users now chat together in "Tech"

### Scenario 2: Private Messaging
1. **User A** and **User B** in different rooms
2. **User A** sends private message to **User B**
3. **User B** receives private message instantly
4. Both can view private message history

### Scenario 3: Real-time Notifications
1. **User A** online in "General"
2. **User B** joins the chat
3. **User A** sees "User B is now online"
4. **User B** joins "General" room
5. **User A** sees "User B joined General"

### Scenario 4: Message Persistence
1. Users send messages in various rooms
2. Server automatically saves to `data/message_history.dat`
3. Server restart doesn't lose message history
4. Users can view old messages anytime

### Scenario 5: Broadcast System
1. Admin user sends broadcast message
2. All connected users receive it instantly
3. Message appears with [BROADCAST] tag
4. Demonstrates server-to-all-clients communication

## ⚠️ Lưu ý kỹ thuật

### Network Configuration:
- **Port**: 1099 (default RMI Registry port)
- **Host**: localhost (có thể thay đổi thành IP khác)
- **Protocol**: RMI over TCP/IP
- **Firewall**: Ensure port 1099 is open

### Performance:
- **Concurrent Users**: Support nhiều users đồng thời
- **Message Throughput**: Real-time delivery
- **Memory Usage**: Efficient with ConcurrentHashMap
- **Disk I/O**: Async message history saving

### Error Handling:
- **Connection Loss**: Auto-detection and cleanup
- **Invalid Operations**: Graceful error messages
- **User Conflicts**: Prevent duplicate usernames
- **Room Limits**: Maximum users per room
- **Message Validation**: Empty message prevention

## 🎯 Điểm mạnh của RMI trong Chat System

### 1. **Transparency**:
- Gọi remote methods như local methods
- Automatic parameter passing và return values
- No manual socket programming required

### 2. **Object-Oriented**:
- Pass complex objects (User, Message, ChatRoom)
- Maintain object relationships across network
- Type-safe compile-time checking

### 3. **Bidirectional Communication**:
- Server can call back to clients
- Real-time push notifications
- Event-driven architecture

### 4. **Automatic Serialization**:
- Objects automatically converted to byte streams
- No manual JSON/XML parsing
- Preserve object state across network

### 5. **Built-in Features**:
- Registry service discovery
- Garbage collection for remote objects
- Security manager integration
- Exception propagation

## 📈 Comparison với other technologies

| Feature | RMI | Socket | REST API | WebSocket |
|---------|-----|--------|----------|-----------|
| **Ease of Use** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| **Real-time** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Object Support** | ⭐⭐⭐⭐⭐ | ⭐ | ⭐⭐ | ⭐⭐ |
| **Platform** | Java only | Any | Any | Any |
| **Learning Curve** | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

## 🔍 Advanced Features Demo

### 1. **Concurrent User Management**:
```java
private Map<String, User> onlineUsers = new ConcurrentHashMap<>();
```

### 2. **Message Broadcasting**:
```java
for (User user : onlineUsers.values()) {
    user.getClientCallback().receiveBroadcastMessage(message);
}
```

### 3. **Persistent Storage**:
```java
ObjectOutputStream oos = new ObjectOutputStream(
    new FileOutputStream("data/message_history.dat"));
oos.writeObject(messageHistory);
```

### 4. **Real-time Callbacks**:
```java
public interface ChatClientInterface extends Remote {
    void receivePublicMessage(Message message) throws RemoteException;
    void notifyUserJoined(String username, String room) throws RemoteException;
}
```

## 🏆 Kết luận

Demo này thể hiện đầy đủ khả năng của RMI trong việc xây dựng distributed applications:

### ✅ **RMI Concepts Demonstrated**:
- Remote interfaces và implementations
- Object serialization across network
- RMI Registry service discovery
- Bidirectional remote method calls
- Exception handling in distributed environment
- Concurrent access management

### ✅ **Real-world Application**:
- Multi-user real-time communication
- Persistent data storage
- User authentication và session management
- Event notification system
- Scalable server architecture

### ✅ **Professional Features**:
- Clean separation of concerns
- Robust error handling
- User-friendly interfaces
- Comprehensive logging
- Graceful shutdown procedures

**🎓 Perfect cho đồ án Hệ phân tán! Demonstrates both theoretical concepts và practical implementation của RMI technology.**

---

## 📞 Demo Support

Nếu gặp vấn đề khi chạy demo:

1. **Compilation Issues**: Check Java version (requires Java 8+)
2. **Connection Problems**: Ensure server starts before clients
3. **Port Conflicts**: Change port if 1099 is occupied
4. **Firewall Issues**: Allow Java through firewall
5. **Performance**: Test with multiple clients for scalability

**Happy Chatting! 💬🚀**
