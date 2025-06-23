// src/main/java/com/example/workmanagement/repository/TaskRepository.java
package com.example.workmanagement.repository;

import com.example.workmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndProgress(Long userId, String progress);
//    List<Task> findByUserIdAndCompletionTimeBetween(Long userId, LocalDateTime start, LocalDateTime end); // Assuming you add completionTime to Task, or fetch through Complete
    List<Task> findByUserIdAndEndTimeBetween(Long userId, LocalDateTime start, LocalDateTime end); // For tasks due soon
    //List<Task> findByUserIdAndProgressAndCompletionTimeBetween(Long userId, String progress, LocalDateTime start, LocalDateTime end);

    // For dashboard recent activity, fetch tasks by user and order by modified/created time
    List<Task> findByUserIdOrderByTaskIdDesc(Long userId); // Or order by actual modification/creation date
}