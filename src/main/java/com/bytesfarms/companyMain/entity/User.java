package com.bytesfarms.companyMain.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String email;
	private String password;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@OneToOne(optional = true)
	@JoinColumn(name = "profile_id")
	private UserProfile profile;

	private boolean isCheckedInToday;

	private String uuid;

	private String fixedSalary;

	private Date resetTokenExpiration;

}
