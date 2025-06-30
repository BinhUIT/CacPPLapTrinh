package com.example.workmanagement.response;

import java.util.ArrayList;

import com.example.workmanagement.model.User;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class LoginResponse {
    private User user;
    private String token;
    public LoginResponse() {
        user.setListTask(new ArrayList<>()); 


    }
    public LoginResponse(User user, String token) {
        this.user = user;
        this.user.setListTask(new ArrayList<>()); 
        this.token=token;
    }
}
