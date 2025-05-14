package com.example.workmanagement.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.workmanagement.model.Complete;
import com.example.workmanagement.model.Task;
import com.example.workmanagement.repository.CompleteRepository;
import com.example.workmanagement.repository.TaskRepository;
import com.example.workmanagement.request.TaskUpdateProgressRequest;
import com.example.workmanagement.response.TaskResponse;

@Service
public class CompleteService {
    private final CompleteRepository completeRepo;
    private final TaskRepository taskRepo;
    private final UserService userService;

    public CompleteService(CompleteRepository completeRepo, TaskRepository taskRepo, UserService userService) {
        this.completeRepo = completeRepo;
        this.taskRepo = taskRepo;
        this.userService = userService;
    }

    public void updateProgress(Task task, float completeProgress) {
        task.setCurrentProgress(completeProgress);
        if (completeProgress == task.getProgress()) {
            Complete complete = new Complete();
            complete.setTask(task);
            complete.setCompleteTime(new Date());
            task.setComplete(complete);
            completeRepo.save(complete);

        }

        taskRepo.save(task);
        if (task.getParent() != null) {

            updateProgress(task.getParent(), completeProgress);
        }
    }

    public TaskResponse updateTaskProgress(TaskUpdateProgressRequest request, String email) throws Exception {
        Task task = taskRepo.findById(request.getTaskId()).orElse(null);

        if (task == null) {
            throw new Exception("404");
        }
        if (!task.getUser().getEmail().equals(email)) {
            throw new Exception("401");
        }
        if (request.getCompleteProgress() > task.getProgress()) {
            throw new Exception("400");
        }
        updateProgress(task, request.getCompleteProgress());
        return new TaskResponse(task);

    }
}
