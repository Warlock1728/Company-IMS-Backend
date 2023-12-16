package com.bytesfarms.companyMain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resume;
    private String coverLetter;
    private LocalDateTime applicationDate;
    private boolean shortlisted;
    private boolean interviewScheduled;

    @ManyToOne
    @JoinColumn(name = "job_position_id")
    private JobPosition jobPosition;
}
