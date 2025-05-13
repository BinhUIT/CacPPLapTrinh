package com.example.workmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    public List<Task> findByUser(User user);
}
