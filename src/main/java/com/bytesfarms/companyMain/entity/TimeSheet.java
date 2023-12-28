package com.bytesfarms.companyMain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytesfarms.companyMain.util.TimeSheetStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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

	private String day;

	private LocalDateTime checkInTime;

	private LocalDateTime checkOutTime;

	@OneToMany(mappedBy = "timeSheet", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Break> breaks = new ArrayList<>();

	private TimeSheetStatus status;

	private long ActualHours;
	private long ActualMinutes;
	private long ActualSeconds;

	private String month;

	private String year;

}
