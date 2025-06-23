// src/main/java/com/example/workmanagement/model/Complete.java
package com.example.workmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "complete")
@Data
public class Complete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completeid")
    private Long completeId;

    // This is the direct foreign key column
    @Column(name = "taskid", nullable = false, insertable = false, updatable = false) // Make it read-only if you use the @ManyToOne
    private Long taskId; // Foreign key to Task.taskId

    // Define the Many-to-One relationship to Task
    @ManyToOne
    @JoinColumn(name = "taskid", referencedColumnName = "taskid") // Connects to the taskid column in Task table
    private Task task; // This field will hold the associated Task object

    @Column(name = "sessionid", nullable = false, insertable = false, updatable = false) // Same for session
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "sessionid", referencedColumnName = "sessionid")
    private Session session;

    @Column(name = "completiontime")
    private LocalDateTime completionTime;

    // Helper for display, not directly mapped to DB column
    @Transient
    private String taskName; // Now you can get taskName from task.getName()
    @Transient
    private long durationMinutes;

    // Constructors, getters, setters provided by Lombok @Data
}