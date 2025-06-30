package com.example.workmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.workmanagement.model.User;
import com.example.workmanagement.model.UserPrincipal;
import com.example.workmanagement.repository.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

    private UserRepository userRepo;

    public MyUserDetailService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

        List<User> listUser = userRepo.findByEmail(userEmail);
        if (listUser.isEmpty()) {
            throw new UsernameNotFoundException("User id not found");
        }
        return new UserPrincipal(listUser.get(0));
    }

}
