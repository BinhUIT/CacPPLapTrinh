package com.example.workmanagement.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SplitTaskRequest {
    private int parentId;
    private List<CreateChildTaskRequest> listRequest;
}
