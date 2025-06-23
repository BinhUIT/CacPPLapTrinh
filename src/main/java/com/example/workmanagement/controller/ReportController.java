// src/main/java/com/example/workmanagement/controller/ReportController.java
package com.example.workmanagement.controller;

import com.example.workmanagement.model.Complete;
import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.Task;
// Không cần import Repository nữa vì chúng ta không dùng DB lúc này
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.Duration; // Để tính toán thời gian
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Để lọc dữ liệu mẫu

@Controller
public class ReportController {

    // Không cần @Autowired các Repository nữa khi dùng dữ liệu mẫu
    // @Autowired
    // private CompleteRepository completeRepository;
    // @Autowired
    // private SessionRepository sessionRepository;
    // @Autowired
    // private TaskRepository taskRepository;

    @GetMapping("/reports")
    public String showReports(@RequestParam(value = "period", defaultValue = "thisMonth") String period, Model model) {
        // --- DỮ LIỆU MẪU (MOCK DATA) ---
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate = now;

        switch (period) {
            case "today":
                startDate = now.toLocalDate().atStartOfDay();
                break;
            case "last7days":
                startDate = now.minusDays(7).toLocalDate().atStartOfDay();
                break;
            case "thisMonth":
                startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay(); // Lấy ngày đầu tháng
                break;
            case "allTime":
            default:
                startDate = LocalDateTime.of(2020, 1, 1, 0, 0); // Một ngày nào đó rất xa trong quá khứ
                break;
        }

        // Tạo danh sách Task mẫu
        List<Task> mockTasks = new ArrayList<>();
        Task taskA = new Task(); taskA.setTaskId(101L); taskA.setName("Hoàn thành báo cáo dự án X"); taskA.setUserId(1L);
        Task taskB = new Task(); taskB.setTaskId(102L); taskB.setName("Họp team Sprint Review"); taskB.setUserId(1L);
        Task taskC = new Task(); taskC.setTaskId(103L); taskC.setName("Nghiên cứu công nghệ mới"); taskC.setUserId(1L);
        mockTasks.add(taskA); mockTasks.add(taskB); mockTasks.add(taskC);

        // Tạo danh sách Complete mẫu
        List<Complete> mockCompletes = new ArrayList<>();
        Complete comp1 = new Complete();
        comp1.setCompleteId(1L); comp1.setTaskId(101L); comp1.setCompletionTime(LocalDateTime.of(2025, 6, 20, 14, 0)); comp1.setTaskName("Hoàn thành báo cáo dự án X");
        mockCompletes.add(comp1);

        Complete comp2 = new Complete();
        comp2.setCompleteId(2L); comp2.setTaskId(102L); comp2.setCompletionTime(LocalDateTime.of(2025, 6, 18, 16, 30)); comp2.setTaskName("Họp team Sprint Review");
        mockCompletes.add(comp2);

        Complete comp3 = new Complete();
        comp3.setCompleteId(3L); comp3.setTaskId(103L); comp3.setCompletionTime(LocalDateTime.of(2025, 5, 25, 11, 0)); comp3.setTaskName("Nghiên cứu công nghệ mới");
        mockCompletes.add(comp3);


        // Tạo danh sách Session mẫu
        List<Session> mockSessions = new ArrayList<>();
        Session sess1 = new Session();
        sess1.setSessionId(201L); sess1.setUserId(1L); sess1.setStartTime(LocalDateTime.of(2025, 6, 20, 9, 0)); sess1.setEndTime(LocalDateTime.of(2025, 6, 20, 12, 0)); // 3 hours
        mockSessions.add(sess1);

        Session sess2 = new Session();
        sess2.setSessionId(202L); sess2.setUserId(1L); sess2.setStartTime(LocalDateTime.of(2025, 6, 19, 13, 0)); sess2.setEndTime(LocalDateTime.of(2025, 6, 19, 17, 30)); // 4.5 hours
        mockSessions.add(sess2);

        Session sess3 = new Session();
        sess3.setSessionId(203L); sess3.setUserId(1L); sess3.setStartTime(LocalDateTime.of(2025, 5, 10, 8, 0)); sess3.setEndTime(LocalDateTime.of(2025, 5, 10, 16, 0)); // 8 hours
        mockSessions.add(sess3);


        // --- Lọc dữ liệu mẫu theo khoảng thời gian đã chọn ---
        List<Complete> completedTasks = mockCompletes.stream()
                .filter(c -> !c.getCompletionTime().isBefore(startDate) && !c.getCompletionTime().isAfter(endDate))
                .collect(Collectors.toList());

        List<Session> workSessions = mockSessions.stream()
                .filter(s -> !s.getStartTime().isBefore(startDate) && !s.getStartTime().isAfter(endDate))
                .collect(Collectors.toList());

        // --- Tính toán thống kê từ dữ liệu mẫu đã lọc ---
        long totalCompletedTasks = completedTasks.size();

        long totalWorkMinutes = workSessions.stream()
                .mapToLong(session -> Duration.between(session.getStartTime(), session.getEndTime()).toMinutes())
                .sum();
        double totalWorkHours = totalWorkMinutes / 60.0;

        double avgCompletionMinutes = 0;
        if (totalCompletedTasks > 0) {
            // Đối với avgCompletionMinutes, bạn cần thời gian thực hiện của từng task.
            // Nếu bạn không lưu duration vào `Complete` entity,
            // bạn sẽ cần tính toán từ StartTime của Task và CompletionTime của Complete.
            // Ví dụ này chỉ lấy trung bình của một số giá trị mẫu:
            // Giả sử có 2 task hoàn thành trong 180 phút và 270 phút
            double totalDurationOfCompletedTasks = 0;
            if (completedTasks.size() > 0) {
                // Đây là chỗ phức tạp nếu không có trường duration trong Complete.
                // Để đơn giản, giả sử mỗi task hoàn thành trung bình mất 45 phút.
                totalDurationOfCompletedTasks = completedTasks.size() * 45; // ví dụ
            }
            avgCompletionMinutes = totalDurationOfCompletedTasks / totalCompletedTasks;
        }


        // --- Thêm dữ liệu mẫu đã tính toán vào Model ---
        model.addAttribute("period", period);
        model.addAttribute("totalCompletedTasks", totalCompletedTasks);
        model.addAttribute("totalWorkHours", totalWorkHours);
        model.addAttribute("avgCompletionMinutes", avgCompletionMinutes);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("workSessions", workSessions);

        return "reports";
    }
}