package com.bytesfarms.companyMain.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytesfarms.companyMain.util.TimeSheetStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class TimeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @OneToMany(mappedBy = "timeSheet", cascade = CascadeType.ALL)
    private List<Break> breaks = new ArrayList<>();

    private TimeSheetStatus status;
}

