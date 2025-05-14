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

import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.User;
import com.example.workmanagement.request.CreateChildTaskRequest;
import com.example.workmanagement.request.CreateTaskRequest;
import com.example.workmanagement.request.LoginRequest;
import com.example.workmanagement.request.SplitTaskRequest;
import com.example.workmanagement.request.TaskUpdateProgressRequest;
import com.example.workmanagement.response.LoginResponse;
import com.example.workmanagement.response.TaskResponse;
import com.example.workmanagement.service.CompleteService;
import com.example.workmanagement.service.JWTService;
import com.example.workmanagement.service.SessionService;
import com.example.workmanagement.service.TaskService;
import com.example.workmanagement.service.UserService;

@RestController
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;
    private final TaskService taskService;
    private final SessionService sessionService;
    private final CompleteService completeService;

    public UserController(UserService userService, JWTService jwtService, TaskService taskService,
            SessionService sessionService, CompleteService completeService) {
        this.taskService = taskService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.completeService = completeService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User res = userService.register(user);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().equals("User exist")) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            String[] messages = { "Invalid email", "Invalid password", "Invalid phone" };
            if (e.getMessage().equals(messages[0]) || e.getMessage().equals(messages[1])
                    || e.getMessage().equals(messages[2])) {
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
        } catch (Exception e) {
            if (e.getMessage().equals("Forbidden")) {
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
    public ResponseEntity<TaskResponse> createNewTask(@RequestHeader("Authorization") String authHeader,
            @RequestBody CreateTaskRequest request) {
        try {
            String email = jwtService.extractEmail(authHeader.substring(7));
            return new ResponseEntity<>(taskService.createNewTask(request, email), HttpStatus.OK);
        }

        catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Invalid start and end time")) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/split/task")
    public ResponseEntity<TaskResponse> splitTask(@RequestHeader("Authorization") String authHeader,
            @RequestBody SplitTaskRequest request) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        try {
            return new ResponseEntity<>(taskService.splitTask(request, email), HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().equals("400") || e.getMessage().equals("401") || e.getMessage().equals("404")) {
                int statusCode = Integer.parseInt(e.getMessage());
                HttpStatus status;
                switch (statusCode) {
                    case 400:
                        status = HttpStatus.BAD_REQUEST;
                        break;
                    case 401:
                        status = HttpStatus.UNAUTHORIZED;
                        break;
                    case 404:
                        status = HttpStatus.NOT_FOUND;
                        break;
                    default:
                        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(null, status);
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/start_session")
    public ResponseEntity<Session> startSession(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(sessionService.startSession(email), HttpStatus.OK);
    }

    @GetMapping("/end_session")
    public ResponseEntity<Session> endSession(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(sessionService.endSession(email), HttpStatus.OK);
    }

    @PostMapping("/update/progress")
    public ResponseEntity<TaskResponse> updateTaskProgress(@RequestHeader("Authorization") String authHeader,
            @RequestBody TaskUpdateProgressRequest request) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        try {
            return new ResponseEntity<>(completeService.updateTaskProgress(request, email), HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().equals("400") || e.getMessage().equals("401") || e.getMessage().equals("404")) {
                int statusCode = Integer.parseInt(e.getMessage());
                HttpStatus status;
                switch (statusCode) {
                    case 400:
                        status = HttpStatus.BAD_REQUEST;
                        break;
                    case 401:
                        status = HttpStatus.UNAUTHORIZED;
                        break;
                    case 404:
                        status = HttpStatus.NOT_FOUND;
                        break;
                    default:
                        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(null, status);
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
