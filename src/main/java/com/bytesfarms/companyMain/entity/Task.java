package com.bytesfarms.companyMain.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String taskDescription;
	private LocalDate date;
	private LocalTime expectedTime;
	private LocalTime actualTime;
	
	
    private String status;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
