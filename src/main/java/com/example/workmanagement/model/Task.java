package com.example.workmanagement.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    
    private int taskId;

    @ManyToOne
    @JoinColumn(name="userid") 
    private User user;

    private String name;
    private Date startTime;
    private Date endTime;
    private String description;
    private float progress;
    private float currentProgress; 
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Task parent;

    @OneToMany(mappedBy = "parent")
    private List<Task> children;
    
    public Task(User user, String name, Date startTime, Date endTime, String description, float progress, float currentProgress) {
        this.user = user;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.progress = progress;
        this.currentProgress= currentProgress;
    }
    
    
}
