package com.example.workmanagement.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskResponse {
    private int taskId;
    private User user;
    private String name;
    private Date startTime;
    private Date endTime;
    private String description;
    private float progress;
    private float currentProgress;
    private List<TaskResponse> child;

    public TaskResponse(Task task) {
        this.child = new ArrayList<>();
        this.taskId = task.getTaskId();
        this.user = task.getUser();
        this.user.setListTask(new ArrayList<>());
        this.user.setListSession(new ArrayList<>());
        this.name = task.getName();
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
        this.description = task.getDescription();
        this.progress = task.getProgress();
        this.currentProgress = task.getCurrentProgress();

        for (int i = 0; i < task.getChildren().size(); i++) {
            this.child.add(new TaskResponse(task.getChildren().get(i)));
            this.child.get(i).user = null;
        }
    }
}
