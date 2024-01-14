package com.bytesfarms.companyMain.dto;

import com.bytesfarms.companyMain.entity.Role;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class UserDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String email;
	private String password;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

}
