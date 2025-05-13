package com.example.workmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.TaskRepository;
import com.example.workmanagement.repository.UserRepository;
import com.example.workmanagement.request.CreateChildTaskRequest;
import com.example.workmanagement.request.CreateTaskRequest;
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
    public TaskService(TaskRepository taskRepo, EntityManager entityManager, UserRepository userRepo) {
        this.entityManager=entityManager;
        this.taskRepo= taskRepo;
        this.userRepo= userRepo;
    } 
    public TaskResponse getTaskById(int taskId) {
        Task task= taskRepo.findById(taskId).orElse(null);
        if(task==null) {
            return null;
        } 
        return new TaskResponse(task);
    }
    public List<TaskResponse> getTaskByEmail(String email) 
    {
        CriteriaBuilder cb= entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cQuery = cb.createQuery(Task.class);
        Root<Task> rootTask = cQuery.from(Task.class);
        Join<Task,User> userJoin = rootTask.join("user");
        Predicate emailPredicate=cb.equal(userJoin.get("email"), email);
        Predicate isRootPredicate = cb.isNull(rootTask.get("parent"));
        cQuery.select(rootTask).where(cb.and(emailPredicate,isRootPredicate));
        List<Task> result=entityManager.createQuery(cQuery).getResultList();
        return this.getListTaskResponseFromListTask(result);
    }
    private List<TaskResponse> getListTaskResponseFromListTask(List<Task> listTask) {
        List<TaskResponse> listTaskResponse = new ArrayList<>();
        for(int i=0;i<listTask.size();i++) {
            listTaskResponse.add(new TaskResponse(listTask.get(i)));
        } 
        return listTaskResponse;
    }

    public TaskResponse createNewTask(CreateTaskRequest request, String email) {
        List<User> listUser = userRepo.findByEmail(email);
        if(listUser==null||listUser.isEmpty()) {
            return null;
        }
        Task task= new Task(request, listUser.get(0));
        taskRepo.save(task);
        return new TaskResponse(task);
    }
    public TaskResponse createChildTask(CreateChildTaskRequest request, String email, int parentId) throws Exception {
        Task parent = taskRepo.findById(parentId).orElse(null);
        if(parent==null) {
            return null;
        } 
        if(!parent.getUser().getEmail().equals(email)) {
            return null;
        }
        if(request.getStartTime().before(parent.getStartTime())) {
            throw new Exception("Child task can not start before its parent");
        } 
        if(request.getEndTime().after(parent.getEndTime())) {
            throw new Exception("Child task can not end after its parent");
        }
        Task task = new Task(request, parent.getUser());
        task.setParent(parent);
        task.setProgress(request.getProgress()); 
        taskRepo.save(task);
        return new TaskResponse(task);
    }
}
