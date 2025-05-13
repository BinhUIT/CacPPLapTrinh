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
    @Autowired
    private AuthenticationManager authManager;

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
        userRepository.save(new User(user));
        return user;
    }
    public LoginResponse login(LoginRequest request) throws Exception {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if(authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(request.getEmail()).get(0);
            return new LoginResponse(user,jwtService.generateToken(request.getEmail()));
        }
        throw new Exception("Forbidden");
    }
    

}
