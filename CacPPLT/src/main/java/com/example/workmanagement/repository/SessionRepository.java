package com.example.workmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.workmanagement.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

}
