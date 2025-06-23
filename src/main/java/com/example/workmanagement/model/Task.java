// src/main/java/com/example/workmanagement/model/Task.java
package com.example.workmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskid")
    private Long taskId;

    @Column(name = "userid", nullable = false)
    private Long userId; // Foreign key to User.userId

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "starttime")
    private LocalDateTime startTime;

    @Column(name = "endtime")
    private LocalDateTime endTime;

    @Column(name = "description")
    private String description;

    @Column(name = "progress") // e.g., "Pending", "In Progress", "Completed"
    private String progress;

    @Column(name = "parentid")
    private Long parentId; // Self-referencing foreign key for sub-tasks

    // Constructors, getters, setters provided by Lombok @Data
}