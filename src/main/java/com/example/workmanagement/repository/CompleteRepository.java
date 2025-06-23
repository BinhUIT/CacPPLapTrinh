// src/main/java/com/example/workmanagement/repository/CompleteRepository.java
package com.example.workmanagement.repository;

import com.example.workmanagement.model.Complete;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface CompleteRepository extends JpaRepository<Complete, Long> {
    List<Complete> findByTaskId(Long taskId); // To find completions for a specific task
    List<Complete> findByCompletionTimeBetween(LocalDateTime start, LocalDateTime end); // For general completion reports
    List<Complete> findByTaskUserIdAndCompletionTimeBetween(Long userId, LocalDateTime start, LocalDateTime end); // If you want to join through Task
}