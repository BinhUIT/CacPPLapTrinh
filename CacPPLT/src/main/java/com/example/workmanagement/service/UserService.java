package com.example.workmanagement.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.workmanagement.middleware.CheckRegisterInfo;
import com.example.workmanagement.middleware.FilterUserInfo;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.UserRepository;
import com.example.workmanagement.request.LoginRequest;
import com.example.workmanagement.request.UserUpdateRequest;
import com.example.workmanagement.response.LoginResponse;

@Service
public class UserService {

    private AuthenticationManager authManager;

    private UserRepository userRepository;

    private JWTService jwtService;

    private CheckRegisterInfo checkRegisterInfo;
    private FilterUserInfo filterUserInfo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(AuthenticationManager authManager, UserRepository userRepository, JWTService jwtService,
            CheckRegisterInfo checkRegisterInfo, FilterUserInfo filterUserInfo) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.checkRegisterInfo = checkRegisterInfo;
        this.filterUserInfo= filterUserInfo;
    }

    public User register(User user) throws Exception {
        List<User> existUser = userRepository.findByEmail(user.getEmail());
        if (!existUser.isEmpty()) {
            throw new Exception("User exist");
        }
        if (!checkRegisterInfo.validEmail(user.getEmail())) {
            throw new Exception("Invalid email");
        }
        /*
         * if(!checkRegisterInfo.validPhone(user.getPhone())) {
         * throw new Exception("Invalid phone");
         * }
         */
        if (!checkRegisterInfo.validPassword(user.getPassword())) {
            throw new Exception("Invalid password");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(new User(user));
        filterUserInfo.filterUserInfo(user);
        return user;
    }

    public LoginResponse login(LoginRequest request) throws Exception {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(request.getEmail()).get(0);
            filterUserInfo.filterUserInfo(user);
            return new LoginResponse(user, jwtService.generateToken(request.getEmail()));
        }
        throw new Exception("Forbidden");
    }

    public User findUserByEmail(String email) {
        List<User> listUser = userRepository.findByEmail(email);
        if (listUser == null || listUser.isEmpty()) {
            return null;
        }
        return listUser.get(0);
    }
    public void updateUser(User user, UserUpdateRequest request) {
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        userRepository.save(user);
    }
}
