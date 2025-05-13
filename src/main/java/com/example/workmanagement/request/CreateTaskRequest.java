package com.example.workmanagement.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTaskRequest {
    protected String name;
    protected Date endTime;
    protected Date startTime;
    protected String description;
    
}
