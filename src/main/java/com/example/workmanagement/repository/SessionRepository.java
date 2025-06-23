// src/main/java/com/example/workmanagement/repository/SessionRepository.java
package com.example.workmanagement.repository;

import com.example.workmanagement.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}