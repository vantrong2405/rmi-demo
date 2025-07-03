# BÁO CÁO ĐỒ ÁN: KỸ THUẬT RMI (REMOTE METHOD INVOCATION)

**Sinh viên thực hiện**: Nguyễn Tấn Thành Long

## PHẦN 1: GIỚI THIỆU TỔNG QUAN

### 1.1. Định Nghĩa RMI
Remote Method Invocation (RMI) là một cơ chế cho phép một đối tượng trong một Java Virtual Machine (JVM) có thể gọi các phương thức của một đối tượng trong một JVM khác. Hai JVM này có thể nằm trên cùng một máy tính hoặc trên các máy tính khác nhau được kết nối qua mạng.

### 1.2. Đặc Điểm Chính
- Là một API của Java cho phép gọi phương thức từ xa
- Hoạt động trong môi trường phân tán (distributed environment)
- Tự động xử lý việc truyền dữ liệu giữa các máy
- Hỗ trợ garbage collection phân tán

### 1.3. Mục Đích Sử Dụng
- Xây dựng ứng dụng phân tán
- Tách biệt logic xử lý giữa client và server
- Chia sẻ tài nguyên và dịch vụ qua mạng
- Tăng khả năng mở rộng của hệ thống

## PHẦN 2: KIẾN TRÚC VÀ CÁC THÀNH PHẦN

### 2.1. Kiến Trúc Tổng Thể
```
[Client Program] ──> [RMI Registry] <──> [Server Program]
      │                                         │
      └─── Stub Object                  Remote Object ─┘
```

### 2.2. Các Thành Phần Chính

#### 2.2.1. Remote Interface
- Định nghĩa các phương thức có thể được gọi từ xa
- Kế thừa từ interface java.rmi.Remote
- Mọi phương thức phải throw RemoteException

```java
public interface HelloInterface extends Remote {
    String sayHello() throws RemoteException;
}
```

#### 2.2.2. Remote Object
- Triển khai Remote Interface
- Kế thừa từ UnicastRemoteObject
- Chứa logic xử lý thực tế

```java
public class HelloImpl extends UnicastRemoteObject implements HelloInterface {
    public String sayHello() throws RemoteException {
        return "Hello from RMI Server!";
    }
}
```

#### 2.2.3. RMI Registry
- Dịch vụ naming để đăng ký và tìm kiếm remote objects
- Hoạt động như một DNS server cho remote objects
- Mặc định chạy trên port 1099

#### 2.2.4. Stub và Skeleton
- **Stub**: Đại diện cho remote object ở phía client
- **Skeleton**: Xử lý các cuộc gọi từ xa ở phía server

## PHẦN 3: NGUYÊN LÝ HOẠT ĐỘNG

### 3.1. Quy Trình Hoạt Động

1. **Khởi Động Server**:
   ```java
   HelloImpl obj = new HelloImpl();
   Registry registry = LocateRegistry.createRegistry(1099);
   registry.bind("HelloService", obj);
   ```

2. **Client Lookup và Gọi Phương Thức**:
   ```java
   Registry registry = LocateRegistry.getRegistry("localhost", 1099);
   HelloInterface stub = (HelloInterface) registry.lookup("HelloService");
   String response = stub.sayHello();
   ```

### 3.2. Các Bước Chi Tiết

1. **Đăng Ký Service**:
   - Server tạo instance của remote object
   - Đăng ký object với RMI Registry
   - Registry lưu trữ reference và tên service

2. **Client Lookup**:
   - Client tìm kiếm service theo tên
   - Registry trả về stub object
   - Stub được tạo động và serialized

3. **Remote Method Call**:
   - Client gọi phương thức trên stub
   - Stub đóng gói parameters
   - Gửi request qua network
   - Skeleton nhận và giải mã request
   - Thực thi phương thức thực tế
   - Trả kết quả ngược lại qua các bước tương tự

## PHẦN 4: ƯU, KHUYẾT ĐIỂM

### 4.1. Ưu Điểm
1. **Dễ Sử Dụng**:
   - Syntax tự nhiên như gọi method thông thường
   - Không cần xử lý chi tiết network
   - Tự động serialize/deserialize objects

2. **Tính Linh Hoạt**:
   - Có thể pass và return objects
   - Hỗ trợ callbacks từ server về client
   - Dễ dàng mở rộng hệ thống

3. **Bảo Mật**:
   - Tích hợp với Security Manager
   - Hỗ trợ SSL/TLS
   - Kiểm soát truy cập dễ dàng

4. **Hiệu Năng**:
   - Kết nối được tối ưu
   - Hỗ trợ connection pooling
   - Garbage collection tự động

### 4.2. Khuyết Điểm
1. **Giới Hạn Platform**:
   - Chỉ hoạt động trong môi trường Java
   - Không thể giao tiếp với các ngôn ngữ khác
   - Phụ thuộc vào JVM

2. **Vấn Đề Về Network**:
   - Sensitive với network latency
   - Có thể bị block bởi firewall
   - Khó xử lý network failures

3. **Hiệu Năng**:
   - Overhead do serialization
   - Chậm hơn local method calls
   - Tiêu tốn băng thông

4. **Phức Tạp**:
   - Setup môi trường phức tạp
   - Debug khó khăn
   - Cần quản lý exceptions

## PHẦN 5: CÁC LĨNH VỰC ỨNG DỤNG

### 5.1. Ứng Dụng Enterprise
1. **Hệ Thống Phân Tán**:
   - Ứng dụng client-server
   - Hệ thống xử lý giao dịch
   - Phần mềm quản lý doanh nghiệp

2. **Ứng Dụng Tài Chính**:
   - Hệ thống ngân hàng
   - Trading platforms
   - Payment gateways

### 5.2. Ứng Dụng Học Thuật
1. **Hệ Thống Giáo Dục**:
   - E-learning platforms
   - Virtual laboratories
   - Online testing systems

2. **Nghiên Cứu**:
   - Distributed computing
   - Grid computing
   - Scientific simulations

### 5.3. Ứng Dụng Thực Tế
1. **Chat Systems**:
   - Instant messaging
   - Group chat
   - Video conferencing

2. **Game Online**:
   - Multiplayer games
   - Game servers
   - Real-time gaming

3. **IoT và Monitoring**:
   - Sensor networks
   - Remote monitoring
   - Device management

### 5.4. Demo Ứng Dụng Chat
Trong đồ án này, chúng tôi đã xây dựng một ứng dụng chat để minh họa việc sử dụng RMI:

1. **Chức Năng**:
   - Chat nhóm thời gian thực
   - Quản lý người dùng online
   - Lưu trữ lịch sử chat

2. **Triển Khai**:
   - Server xử lý tin nhắn và quản lý users
   - Client với giao diện Java Swing
   - RMI cho giao tiếp hai chiều

3. **Kết Quả**:
   - Hệ thống hoạt động ổn định
   - Thời gian phản hồi nhanh
   - Dễ dàng mở rộng

## TÀI LIỆU THAM KHẢO

1. Oracle Java RMI Documentation
2. Java Network Programming, 4th Edition
3. Distributed Systems: Principles and Paradigms
4. Enterprise Integration Patterns
5. Java RMI Tutorial (Oracle)
