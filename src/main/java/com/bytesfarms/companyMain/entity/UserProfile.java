package com.bytesfarms.companyMain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

@Entity
@Data
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true)
	private String dob;

	@Column(nullable = true)
	private String mobile;

	@Column(nullable = true)
	private String address;

	@Column(nullable = true)
	private Integer age = 0;

	@Column(nullable = true)
	private String gender;

	@Column(nullable = true)
	private String maritalStatus;

	@Column(nullable = true)
	private String designation;

	@Column(nullable = true)
	private String phone;

	@Column(nullable = true)
	private String location;

	@Column(nullable = true)
	private String experience;

	@Column(nullable = true)
	private String joiningDate;

	@OneToOne
	private User user;
}