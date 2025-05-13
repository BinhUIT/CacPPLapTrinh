package com.example.workmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.TaskRepository;
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
    public TaskService(TaskRepository taskRepo, EntityManager entityManager) {
        this.entityManager=entityManager;
        this.taskRepo= taskRepo;
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
}
