// src/main/java/com/example/workmanagement/controller/DashboardController.java
package com.example.workmanagement.controller;

import com.example.workmanagement.model.Task;
// Import các model khác nếu cần, ví dụ: Session, User (nếu bạn tạo)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // --- DỮ LIỆU MẪU (MOCK DATA) ---

        // Dữ liệu người dùng mẫu
        String username = "Tên Người Dùng Mẫu"; // Bạn có thể thay đổi để hiển thị "User" hoặc "John Doe"

        // Dữ liệu Task mẫu
        int tasksDueSoonCount = 3; // Ví dụ: 3 task sắp đến hạn

        // Dữ liệu Session mẫu
        boolean activeSession = true; // Ví dụ: đang có phiên làm việc
        LocalDateTime activeSessionStartTime = LocalDateTime.of(2025, 6, 20, 9, 0); // Ví dụ: bắt đầu lúc 9h sáng hôm nay

        // Dữ liệu Task đã hoàn thành hôm nay
        int completedTodayCount = 1; // Ví dụ: 1 task đã hoàn thành hôm nay

        // Danh sách Task gần đây mẫu
        List<Task> recentTasks = new ArrayList<>();
        // Task 1
        Task task1 = new Task();
        task1.setTaskId(1L);
        task1.setName("Thiết kế UI đăng ký/đăng nhập");
        task1.setProgress("In Progress");
        task1.setEndTime(LocalDateTime.of(2025, 6, 22, 17, 0)); // Đến hạn 22/06
        recentTasks.add(task1);

        // Task 2
        Task task2 = new Task();
        task2.setTaskId(2L);
        task2.setName("Xây dựng Dashboard cơ bản");
        task2.setProgress("Completed");
        task2.setEndTime(LocalDateTime.of(2025, 6, 19, 10, 30)); // Hoàn thành 19/06
        recentTasks.add(task2);

        // Task 3
        Task task3 = new Task();
        task3.setTaskId(3L);
        task3.setName("Viết báo cáo hàng tuần");
        task3.setProgress("Pending");
        task3.setEndTime(LocalDateTime.of(2025, 6, 25, 12, 0)); // Đến hạn 25/06
        recentTasks.add(task3);


        // --- Thêm dữ liệu mẫu vào Model để truyền sang Thymeleaf ---
        model.addAttribute("username", username);
        model.addAttribute("tasksDueSoonCount", tasksDueSoonCount);
        model.addAttribute("activeSession", activeSession);
        model.addAttribute("activeSessionStartTime", activeSessionStartTime);
        model.addAttribute("completedTodayCount", completedTodayCount);
        model.addAttribute("recentTasks", recentTasks);

        return "dashboard";
    }
    @GetMapping("/") // Map với URL gốc của ứng dụng
    public String redirectToDashboard() {
        // Chuyển hướng trình duyệt đến "/dashboard"
        return "redirect:/dashboard";
    }
}