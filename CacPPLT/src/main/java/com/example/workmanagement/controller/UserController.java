package com.example.workmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;
import com.example.workmanagement.request.CreateChildTaskRequest;
import com.example.workmanagement.request.CreateTaskRequest;
import com.example.workmanagement.request.LoginRequest;
import com.example.workmanagement.request.ReportRequest;
import com.example.workmanagement.request.SplitTaskRequest;
import com.example.workmanagement.request.TaskUpdateProgressRequest;
import com.example.workmanagement.request.UserUpdateRequest;
import com.example.workmanagement.response.LoginResponse;
import com.example.workmanagement.response.TaskResponse;
import com.example.workmanagement.service.CompleteService;
import com.example.workmanagement.service.JWTService;
import com.example.workmanagement.service.ReportService;
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
    private final ReportService reportService;

    public UserController(UserService userService, JWTService jwtService, TaskService taskService,
            SessionService sessionService, CompleteService completeService, ReportService reportService) {
        this.taskService = taskService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.completeService = completeService;
        this.reportService= reportService;
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
    @GetMapping("/complete_task")
    public ResponseEntity<List<TaskResponse>> getCompleteTask(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(taskService.getCompleteTaskByEmail(email), HttpStatus.OK);
    }
    @GetMapping("/due_task_in_7_days")
    public ResponseEntity<List<TaskResponse>> getTaskDueIn7Days(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(taskService.getTasksDueInNext7DaysByEmail(email), HttpStatus.OK);
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
    @PostMapping("/update/many_progress") 
    public ResponseEntity<List<TaskResponse>> updateManyTak(@RequestHeader("Authorization") String authHeader, @RequestBody List<TaskUpdateProgressRequest> listRequest) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        List<TaskResponse> res= new ArrayList<>();
        try {
        for(int i=0;i<listRequest.size();i++) {
            res.add(completeService.updateTaskProgress(listRequest.get(i), email));
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
     }
        catch(Exception e) {
            e.printStackTrace();
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
    @GetMapping("/work_time") 
    public ResponseEntity<Long> getTotalWorkTime(@RequestHeader("Authorization") String authHeader) { 
        String email = jwtService.extractEmail(authHeader.substring(7));
        return new ResponseEntity<>(reportService.getTotalWorkTime(email), HttpStatus.OK);
    }
    @GetMapping("/session_in_week") 
    public ResponseEntity<List<Session>> getSessionInWeek(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7)); 
        List<Session> res= reportService.getSessionInWeek(email);
        for(Session s:res) {
            if(s.getStartTime()==null){
                System.out.println("Date is null");
            }
            s.getUser().setListTask(null);
            s.getUser().setListSession(null);
        }
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
    @PostMapping("/get_completed_task") 
    public ResponseEntity<List<TaskResponse>> getCompletedTask(@RequestHeader("Authorization") String authHeader,@RequestBody ReportRequest request) {
         String email = jwtService.extractEmail(authHeader.substring(7)); 
        List<Task> resData= reportService.listCompletedTask(request.getStart(), request.getEnd(), email);
        List<TaskResponse> res = new ArrayList<>();
        for(int i=0;i<resData.size();i++) {
            res.add(new TaskResponse(resData.get(i)));
        } 
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @PutMapping("/update/user")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateRequest request) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findUserByEmail(email);
        userService.updateUser(user, request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
