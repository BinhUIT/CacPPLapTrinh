package com.example.workmanagement.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Complete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int completeId;

    @OneToOne(mappedBy = "complete")

    private Task task;
    private Date completeTime;
}
