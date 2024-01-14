package com.bytesfarms.companyMain.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Payroll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String grossSalary;
	private double netPay;
	private double deductions;
	private double bonus;
	
	private String month;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

}
