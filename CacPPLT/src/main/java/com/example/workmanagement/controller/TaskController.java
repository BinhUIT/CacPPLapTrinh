package com.example.workmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.workmanagement.request.TaskUpdateRequest;
import com.example.workmanagement.response.TaskResponse;
import com.example.workmanagement.service.TaskService;

@RestController
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService= taskService;
    }
    @GetMapping("/task/{id}") 
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable int id) {
        return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
    }
    @PutMapping("/task/update") 
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskUpdateRequest request) {
        try {
            return new ResponseEntity<>(taskService.updateTask(request), HttpStatus.OK);
        } 
        catch(Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("Not found")) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/task/delete/{id}") 
    public ResponseEntity<String> deleteTask(@PathVariable int id) {
        return new ResponseEntity<>(taskService.deleteTask(id), HttpStatus.OK);
    }

}
