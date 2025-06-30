package com.example.workmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    public List<Task> findByUser(User user);
}
