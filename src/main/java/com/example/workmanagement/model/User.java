package com.example.workmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // <-- Import this
import jakarta.persistence.GenerationType; // <-- Import this
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- Use this for auto-incrementing
    @Column(name="userid") // <-- Remove columnDefinition here
    private Long userId; // <-- Change to Long, as primary keys are typically Long (or UUID)

    @Column(name="name", unique = true, length = 50, nullable = false) // Use standard JPA for unique/length/nullable
    private String name;

    @Column(name="email", unique = true, length = 50, nullable = false) // Email should be unique and not null
    private String email;

    @Column(name="phone", unique = true, length = 20, nullable = false) // Phone should be unique and not null
    private String phone;

    @Column(name="password", length = 200, nullable = false)
    private String password;
}
//Code cũ
//package com.example.workmanagement.model;
//
//import java.util.List;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name="user")
//public class User {
//    @Id
//    @Column(name="userid", columnDefinition = "int")
//    @GeneratedValue(strategy=GenerationType.IDENTITY)
//    private int userId;
//
//    @Column(name="name", columnDefinition = "varchar(50)")
//    private String name;
//
//    @Column(name="email", columnDefinition = "varchar(50)")
//    private String email;
//
//    @Column(name="phone", columnDefinition = "varchar(20)")
//    private String phone;
//
//    @Column(name="password", columnDefinition = "varchar(200)")
//    private String password;
//    @OneToMany(mappedBy="user")
//    private List<Task> listTask;
//    @OneToMany(mappedBy="user")
//    private List<Session> listSession;
//
//    public User(User user) {
//        this.name= user.name;
//        this.email= user.email;
//        this.phone=user.phone;
//        this.password= user.password;
//    }
//}