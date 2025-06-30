package com.example.workmanagement.middleware;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.example.workmanagement.model.User;

@Component
public class FilterUserInfo {
    public void filterUserInfo(User user) {
        user.setListSession(new ArrayList<>());
        user.setListTask(new ArrayList<>());

    }
}
