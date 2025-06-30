package com.example.workmanagement.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @Column(name="userid", columnDefinition = "int") 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userId;

    @Column(name="name", columnDefinition = "varchar(50)") 
    private String name;

    @Column(name="email", columnDefinition = "varchar(50)") 
    private String email;

    @Column(name="phone", columnDefinition = "varchar(20)") 
    private String phone;

    @Column(name="password", columnDefinition = "varchar(200)")
    private String password;
    @OneToMany(mappedBy="user") 
    private List<Task> listTask;
    @OneToMany(mappedBy="user") 
    private List<Session> listSession;

    public User(User user) {
        this.name= user.name;
        this.email= user.email;
        this.phone=user.phone;
        this.password= user.password;
    }
}
