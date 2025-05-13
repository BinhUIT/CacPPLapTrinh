package com.example.workmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.workmanagement.model.User;
import com.example.workmanagement.request.CreateChildTaskRequest;
import com.example.workmanagement.request.CreateTaskRequest;
import com.example.workmanagement.request.LoginRequest;
import com.example.workmanagement.response.LoginResponse;
import com.example.workmanagement.response.TaskResponse;
import com.example.workmanagement.service.JWTService;
import com.example.workmanagement.service.TaskService;
import com.example.workmanagement.service.UserService;

@RestController
public class UserController {
    
    private final UserService userService;
    private final JWTService jwtService;
    private final TaskService taskService;
    public UserController(UserService userService, JWTService jwtService, TaskService taskService) {
        this.taskService= taskService;
        this.userService= userService;
        this.jwtService= jwtService;
    }

    @PostMapping("/register") 
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User res = userService.register(user);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } 
        catch(Exception e)  {
            if(e.getMessage().equals("User exist")) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            String[] messages = {"Invalid email","Invalid password", "Invalid phone"};
            if(e.getMessage().equals(messages[0])||e.getMessage().equals(messages[1])||e.getMessage().equals(messages[2])) {
                e.printStackTrace();
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login") 
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse res = userService.login(request);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } 
        catch(Exception e) {
            if(e.getMessage().equals("Forbidden")) {
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all_task") 
    public ResponseEntity<List<TaskResponse>> getAllTask(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(taskService.getTaskByEmail(email), HttpStatus.OK);
    }
    @PostMapping("/create/new_task") 
    public ResponseEntity<TaskResponse> createNewTask(@RequestHeader("Authorization") String authHeader, @RequestBody CreateTaskRequest request) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(taskService.createNewTask(request, email), HttpStatus.OK);
    }
    @PostMapping("/create/child_task/{parentId}") 
    public ResponseEntity<TaskResponse> createNewChildTask(@RequestHeader("Authorization") String authHeader, @RequestBody CreateChildTaskRequest request,@PathVariable int parentId) {
        try {
            String email = jwtService.extractEmail(authHeader.substring(7));
            TaskResponse res = taskService.createChildTask(request, email, parentId);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("Child task can not start before its parent")||e.getMessage().equals("Child task can not end after its parent")) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
