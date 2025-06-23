// src/main/java/com/example/workmanagement/model/Session.java
package com.example.workmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Data
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sessionid")
    private Long sessionId;

    @Column(name = "userid", nullable = false)
    private Long userId; // Foreign key to User.userId

    @Column(name = "starttime")
    private LocalDateTime startTime;

    @Column(name = "endtime")
    private LocalDateTime endTime;

    // Helper for display, not directly mapped to DB column
    @Transient // This field will not be persisted to the database
    private long durationMinutes; // For calculating duration in minutes

    public long getDurationMinutes() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }
    // Constructors, getters, setters provided by Lombok @Data
}