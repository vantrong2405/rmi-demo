# Ứng Dụng Chat Sử Dụng Java RMI

## 1. Tổng Quan Hệ Thống
Đây là ứng dụng chat đơn giản sử dụng Java RMI (Remote Method Invocation) cho phép nhiều người dùng chat với nhau thông qua một server trung tâm.

### Các Thành Phần Chính:
- **Server (ChatServer)**: Quản lý kết nối và điều phối tin nhắn
- **Client (ChatClient)**: Giao diện người dùng để chat
- **RMI Registry**: Dịch vụ đăng ký để kết nối Client-Server

### Công Nghệ Sử Dụng:
- Java RMI cho giao tiếp mạng
- Java Swing cho giao diện đồ họa
- Serialization để truyền dữ liệu
- Multi-threading để xử lý đồng thời

## 2. Chức Năng Chính

### 2.1. Đăng Nhập
- Nhập tên để tham gia chat
- Kiểm tra tên trùng lặp
- Hiển thị thông báo khi người dùng online/offline

### 2.2. Chat
- Gửi tin nhắn công khai trong phòng chat
- Hiển thị tin nhắn với định dạng: [Thời gian] Tên người gửi: Nội dung
- Tự động cuộn xuống tin nhắn mới

### 2.3. Quản Lý Người Dùng
- Hiển thị số người đang online
- Thông báo khi có người tham gia/rời phòng
- Lưu trữ lịch sử tin nhắn

## 3. Cấu Trúc Project

### 3.1. Package `rmi.chat`
- `ChatServer.java`: Server chính
- `ChatClient.java`: Client với giao diện người dùng
- `ChatServerInterface.java`: Interface định nghĩa các phương thức của server
- `ChatClientInterface.java`: Interface định nghĩa các phương thức của client
- `ChatLauncher.java`: Class khởi động ứng dụng

### 3.2. Package `rmi.chat.models`
- `Message.java`: Đối tượng tin nhắn
- `User.java`: Thông tin người dùng
- `ChatRoom.java`: Quản lý phòng chat

## 4. Hướng Dẫn Chạy Trên Netbeans

### 4.1. Khởi Động Server
1. Mở project trong Netbeans
2. Tìm file `ChatServer.java` trong package `rmi.chat`
3. Click chuột phải và chọn "Run File"
4. Server sẽ hiển thị cửa sổ với trạng thái "Server đang chạy"

### 4.2. Khởi Động Client
1. Tìm file `ChatClient.java` trong package `rmi.chat`
2. Click chuột phải và chọn "Run File"
3. Nhập tên trong cửa sổ đăng nhập
4. Bắt đầu chat!

### 4.3. Chạy Nhiều Client
- Lặp lại bước 4.2 để mở nhiều cửa sổ chat
- Mỗi client cần có tên đăng nhập khác nhau

## 5. Lưu Ý Quan Trọng
- Server PHẢI được chạy TRƯỚC khi chạy client
- Mỗi người dùng cần có tên duy nhất
- Tin nhắn được lưu trong thư mục `data/`
- Đảm bảo port 1099 không bị chặn bởi firewall
- Nếu gặp lỗi khi chạy, thử clean và build lại project

## 6. Xử Lý Lỗi Thường Gặp

### 6.1. Lỗi Khi Chạy Server
- **Lỗi port 1099**: Kiểm tra xem có ứng dụng nào đang dùng port 1099 không
- **Lỗi không tạo được thư mục data**: Kiểm tra quyền ghi trong thư mục project

### 6.2. Lỗi Khi Chạy Client
- **Không kết nối được server**: Kiểm tra server đã chạy chưa
- **Lỗi đăng nhập**: Tên có thể đã được sử dụng
- **Không gửi được tin nhắn**: Kiểm tra kết nối với server

### 6.3. Lỗi Netbeans
- Clean và Build lại project
- Xóa thư mục build và dist
- Kiểm tra Java version (yêu cầu Java 8 trở lên)

## 7. Cải Tiến Có Thể Thực Hiện
- Thêm tính năng chat riêng tư
- Tạo nhiều phòng chat
- Thêm emoji và gửi file
- Mã hóa tin nhắn
- Thêm đăng ký tài khoản và mật khẩu
