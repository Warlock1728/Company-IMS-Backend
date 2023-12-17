package com.bytesfarms.companyMain.entity;

import java.time.LocalDateTime;

import com.bytesfarms.companyMain.util.TimeSheetStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	private LocalDateTime checkInTime;

	private LocalDateTime checkOutTime;

	private LocalDateTime breakStartTime;

	private LocalDateTime breakEndTime;

	private TimeSheetStatus status;

}
