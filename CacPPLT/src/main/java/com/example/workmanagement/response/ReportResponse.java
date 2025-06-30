package com.example.workmanagement.response;

import java.util.List;

import com.example.workmanagement.model.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private List<Session> listSession;
    
}
