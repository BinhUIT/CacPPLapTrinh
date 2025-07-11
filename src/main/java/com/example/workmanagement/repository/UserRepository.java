package com.example.workmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.workmanagement.model.User;
@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    public List<User> findByEmail(String email);
}
