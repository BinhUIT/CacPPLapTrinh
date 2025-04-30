package com.example.workmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name="userid", columnDefinition = "int primary key auto_increment") 
    private int userId;

    @Column(name="name", columnDefinition = "varchar(50) unique") 
    private String name;

    @Column(name="email", columnDefinition = "varchar(50)") 
    private String email;

    @Column(name="phone", columnDefinition = "varchar(20)") 
    private String phone;

    @Column(name="password", columnDefinition = "varchar(200)")
    private String password;
}
