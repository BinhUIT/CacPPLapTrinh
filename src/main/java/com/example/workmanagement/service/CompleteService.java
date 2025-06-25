package com.example.workmanagement.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;


import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.Task;

import com.example.workmanagement.repository.TaskRepository;
import com.example.workmanagement.request.TaskUpdateProgressRequest;
import com.example.workmanagement.response.TaskResponse;

import jakarta.transaction.Transactional;

@Service
public class CompleteService {
   
    private final TaskRepository taskRepo;
    private final UserService userService;
    private final SessionService sessionService;

    public CompleteService(TaskRepository taskRepo, UserService userService, SessionService sessionService) {
        
        this.taskRepo = taskRepo;
        this.userService = userService;
        this.sessionService= sessionService;
    }
    @Transactional
    public void updateProgress(Task task, float completeProgress, Session session) {
        task.setCurrentProgress(completeProgress); 
       
        if (task.getCurrentProgress() == task.getProgress()) {
            task.setCompleteTime(new Date());
            
            

        }
        taskRepo.save(task);
        
        
        
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
        List<Session> listSession = sessionService.getWorkingSession(email);
        if(listSession.isEmpty()) {
            return null;
        }
        Session session = listSession.get(0);
        updateProgress(task, request.getCompleteProgress(), session);
        return new TaskResponse(task);

    }
}
