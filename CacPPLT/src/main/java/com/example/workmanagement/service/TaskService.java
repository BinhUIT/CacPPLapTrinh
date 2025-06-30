package com.example.workmanagement.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.workmanagement.middleware.FilterUserInfo;
import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.TaskRepository;
import com.example.workmanagement.repository.UserRepository;
import com.example.workmanagement.request.CreateChildTaskRequest;
import com.example.workmanagement.request.CreateTaskRequest;
import com.example.workmanagement.request.SplitTaskRequest;
import com.example.workmanagement.request.TaskUpdateRequest;
import com.example.workmanagement.response.TaskResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final EntityManager entityManager;
    private final UserRepository userRepo;
    private final FilterUserInfo filterUserInfo;

    public TaskService(TaskRepository taskRepo, EntityManager entityManager, UserRepository userRepo,
            FilterUserInfo filterUserInfo) {
        this.entityManager = entityManager;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.filterUserInfo = filterUserInfo;
    }

    public TaskResponse getTaskById(int taskId) {
        Task task = taskRepo.findById(taskId).orElse(null);
        if (task == null) {
            return null;
        }
        return new TaskResponse(task);
    }

    public List<TaskResponse> getTaskByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cQuery = cb.createQuery(Task.class);
        Root<Task> rootTask = cQuery.from(Task.class);
        Join<Task, User> userJoin = rootTask.join("user");
        Predicate emailPredicate = cb.equal(userJoin.get("email"), email);
        Predicate isRootPredicate = cb.isNull(rootTask.get("parent"));
        cQuery.select(rootTask).where(cb.and(emailPredicate, isRootPredicate));
        List<Task> result = entityManager.createQuery(cQuery).getResultList();
        return this.getListTaskResponseFromListTask(result);
    }
    public List<TaskResponse> getCompleteTaskByEmail(String email){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cQuery = cb.createQuery(Task.class);
        Root<Task> rootTask = cQuery.from(Task.class);
        Join<Task, User> userJoin = rootTask.join("user");
        Predicate emailPredicate = cb.equal(userJoin.get("email"), email);
        Predicate isRootPredicate = cb.isNull(rootTask.get("parent"));
        Predicate progressPredicate = cb.equal(rootTask.get("progress"), 1.0);
        cQuery.select(rootTask).where(cb.and(emailPredicate, isRootPredicate, progressPredicate));
        List<Task> result = entityManager.createQuery(cQuery).getResultList();
        return this.getListTaskResponseFromListTask(result);
    }
    public List<TaskResponse> getTasksDueInNext7DaysByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cQuery = cb.createQuery(Task.class);
        Root<Task> rootTask = cQuery.from(Task.class);
        Join<Task, User> userJoin = rootTask.join("user");

        // 1. Predicate for user email
        Predicate emailPredicate = cb.equal(userJoin.get("email"), email);

        // 2. Predicate for top-level tasks (no parent)
        Predicate isRootPredicate = cb.isNull(rootTask.get("parent"));

        // 3. Predicate for tasks due in the next 7 days
        // Get current time
        LocalDateTime now = LocalDateTime.now();
        // Calculate 7 days from now
        LocalDateTime sevenDaysLater = now.plusDays(7);

        // Convert LocalDateTime to java.util.Date for comparison with entity's Date field
        Date startDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(sevenDaysLater.atZone(ZoneId.systemDefault()).toInstant());

        // Ensure tasks are not yet completed (progress < 100)
        Predicate notCompletedPredicate = cb.lessThan(rootTask.get("progress"), 100f);

        // Check if endTime is between now and sevenDaysLater
        Predicate dueSoonPredicate = cb.between(rootTask.get("endTime"), startDate, endDate);

        // Combine all predicates
        cQuery.select(rootTask)
                .where(cb.and(emailPredicate, isRootPredicate, notCompletedPredicate, dueSoonPredicate));

        // Order by end time to show the soonest tasks first (optional but good for "due soon")
        cQuery.orderBy(cb.asc(rootTask.get("endTime")));

        List<Task> result = entityManager.createQuery(cQuery).getResultList();
        return this.getListTaskResponseFromListTask(result);
    }
    private List<TaskResponse> getListTaskResponseFromListTask(List<Task> listTask) {
        List<TaskResponse> listTaskResponse = new ArrayList<>();
        for (int i = 0; i < listTask.size(); i++) {
            listTaskResponse.add(new TaskResponse(listTask.get(i)));
        }
        return listTaskResponse;
    }

    public TaskResponse createNewTask(CreateTaskRequest request, String email) throws Exception {
        if (request.getEndTime().before(request.getStartTime())) {
            throw new Exception("Invalid start and end time");
        }
        List<User> listUser = userRepo.findByEmail(email);
        if (listUser == null || listUser.isEmpty()) {
            return null;
        }
        Task task = new Task(request, listUser.get(0));
        taskRepo.save(task);
        return new TaskResponse(task);
    }

    private void createChildTask(CreateChildTaskRequest request, Task parent) throws Exception {
        if (request.getEndTime().before(request.getStartTime())) {
            throw new Exception("400");
        }

        if (request.getStartTime().before(parent.getStartTime())) {
            throw new Exception("400");
        }
        if (request.getEndTime().after(parent.getEndTime())) {
            throw new Exception("400");
        }
        Task task = new Task(request, parent.getUser());
        task.setParent(parent);
        task.setProgress(request.getProgress());
        taskRepo.save(task);

    }

    public TaskResponse splitTask(SplitTaskRequest request, String email) throws Exception {
        Task parentTask = taskRepo.findById(request.getParentId()).orElse(null);
        if (parentTask == null) {
            throw new Exception("404");
        }
        if (!parentTask.getUser().getEmail().equals(email)) {
            throw new Exception("401");
        }
        float totalProgress = 0;
        for (int i = 0; i < request.getListRequest().size(); i++) {
            totalProgress += request.getListRequest().get(i).getProgress();
        }
        if (totalProgress != parentTask.getProgress()) {
            throw new Exception("400");
        }
        for (int i = 0; i < request.getListRequest().size(); i++) {
            createChildTask(request.getListRequest().get(i), parentTask);
        }
        taskRepo.save(parentTask);
        return new TaskResponse(parentTask);
    }
    public TaskResponse updateTask(TaskUpdateRequest request) throws Exception {
        Task task = taskRepo.findById(request.getTaskId()).orElse(null);
        if(task==null) {
            throw new Exception("Not found");
        } 
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setEndTime(request.getEndTime());
        taskRepo.save(task);
        return new TaskResponse(task);
    }
    public String deleteTask(int taskId) {
         Task task = taskRepo.findById(taskId).orElse(null);
         if(task==null){
            return "Task not found";
         }
         taskRepo.delete(task); 
         return "Delete task success";
    }
}
