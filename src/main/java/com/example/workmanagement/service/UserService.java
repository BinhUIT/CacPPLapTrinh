package com.example.workmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.workmanagement.middleware.CheckRegisterInfo;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.UserRepository;
import com.example.workmanagement.request.LoginRequest;
import com.example.workmanagement.response.LoginResponse;

@Service
public class UserService {
//    @Autowired
//    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CheckRegisterInfo checkRegisterInfo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public User register(User user) throws Exception {
        List<User> existUser = userRepository.findByEmail(user.getEmail());
        if(!existUser.isEmpty()) {
            throw new Exception("User exist");
        }
        if(!checkRegisterInfo.validEmail(user.getEmail())) {
            throw new Exception("Invalid email");
        }
       /* if(!checkRegisterInfo.validPhone(user.getPhone())) {
            throw new Exception("Invalid phone");
        }*/
        if(!checkRegisterInfo.validPassword(user.getPassword())) {
            throw new Exception("Invalid password");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }
//    public LoginResponse login(LoginRequest request) throws Exception {
//        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//        if(authentication.isAuthenticated()) {
//            User user = userRepository.findByEmail(request.getEmail()).get(0);
//            return new LoginResponse(user,jwtService.generateToken(request.getEmail()));
//        }
//        throw new Exception("Forbidden");
//    }
public LoginResponse login(LoginRequest request) throws Exception {
    // --- HARDCODED MOCK LOGIN ---
    String mockUsername = "bao@gmail.com";
    String mockPassword = "123";

    if (request.getEmail().equals(mockUsername) && request.getPassword().equals(mockPassword)) {
        // Mock successful authentication
        System.out.println("UserService: Mock login successful for email: " + request.getEmail());
        // Create a dummy user object for the response
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail(mockUsername);
        mockUser.setName("Mock User");
        // You might want to mock JWT generation too if jwtService is active
        String mockToken = "MOCK_JWT_TOKEN_12345"; // For now, just a dummy string
        if (jwtService != null) { // Check if jwtService is autowired and not null
            // If jwtService is also completely mocked/disabled, this line will cause an error
            // For now, assume it's okay or comment out if it's the next point of failure
            // mockToken = jwtService.generateToken(request.getEmail());
        }

        return new LoginResponse(mockUser, mockToken);
    } else {
        // Mock failed authentication
        System.out.println("UserService: Mock login failed for email: " + request.getEmail());
        throw new Exception("Invalid credentials"); // Use a more specific exception if desired
    }
}


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
