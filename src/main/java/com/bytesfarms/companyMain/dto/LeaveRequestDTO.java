package com.bytesfarms.companyMain.dto;

import java.time.LocalDate;

import com.bytesfarms.companyMain.entity.User;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class LeaveRequestDTO {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String leaveType;
	private LocalDate startDate;
	private LocalDate endDate;
	
	private String description;
	private String Status;

}