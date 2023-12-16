package com.bytesfarms.companyMain.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;


@Entity
@Data
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String openings;
    private String experience;
    private String requirements;
    
    @JsonIgnore
    @OneToMany(mappedBy = "jobPosition", cascade = CascadeType.ALL)
    private List<JobApplication> applications = new ArrayList<>();

}
