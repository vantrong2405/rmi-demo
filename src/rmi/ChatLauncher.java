/*
 * RMI Chat System Demo Application
 * @author long
 *
 * Main launcher for RMI Chat system
 * This demo shows Real-time Chat communication via Remote Method Invocation
 */
package rmi;

import java.util.Scanner;
import rmi.chat.ChatServer;
import rmi.chat.ChatClient;

public class ChatLauncher {
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        if (args.length > 0) {
            handleChoice(args[0]);
            return;
        }

        while (true) {
            showMenu();
            try {
                String choice = scanner.nextLine().trim();
                if (choice.equals("0")) {
                    shutdownServer();
                    System.out.println("Tạm biệt!");
                    break;
                }
                handleChoice(choice);
            } catch (Exception e) {
                System.err.println("Lỗi: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void showMenu() {

        System.out.println("\n=============================================");
        System.out.println("            PHÒNG CHAT");
        System.out.println("=============================================");

        System.out.println("Chọn chức năng:");
        System.out.println("1. Khởi động Server");
        System.out.println("2. Tham gia phòng chat");
        System.out.println("0. Thoát");
        System.out.print("Lựa chọn của bạn (0-2): ");
    }

    private static void handleChoice(String choice) {
        try {
            switch (choice) {
                case "1":
                    startServer(null);
                    break;
                case "2":
                    startClient(null);
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 0-2.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }

    private static void shutdownServer() {
        try {
            Runtime.getRuntime().exec("pkill -f rmiregistry").waitFor();
            Runtime.getRuntime().exec("pkill -f ChatServer").waitFor();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println("Lỗi khi tắt server: " + e.getMessage());
        }
    }

    private static void startServer(String[] args) {
        System.out.println("\nĐang khởi động Server...");
        System.out.println("Server sẽ chạy tại địa chỉ localhost:1099\n");

        try {
            ChatServer.main(args);
        } catch (Exception e) {
            System.err.println("Không thể khởi động server: " + e.getMessage());
        }
    }

    private static void startClient(String[] args) {
        System.out.println("\nĐang kết nối vào phòng chat...");
        System.out.println("Sẽ kết nối tới localhost:1099");
        System.out.println("Hãy đảm bảo server đang chạy!\n");

        try {
            ChatClient.main(args);
        } catch (Exception e) {
            System.err.println("Không thể kết nối: " + e.getMessage());
            System.out.println("\nNhấn Enter để quay lại menu...");
            scanner.nextLine();
        }
    }
}
