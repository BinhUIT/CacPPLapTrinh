package com.example.workmanagement.middleware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class CheckRegisterInfo {
    public boolean validEmail(String email) {
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    } 
    public boolean validPhone(String phone) {
        Pattern pattern = Pattern.compile("^\\+[1-9]\\\\d{1,14}$");
        Matcher  matcher = pattern.matcher(phone);
        return matcher.find();
    }
    public boolean validPassword(String password) {
        return password.length()>=6;
    }
}
