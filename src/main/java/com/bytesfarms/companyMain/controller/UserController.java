package com.bytesfarms.companyMain.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.dto.UserDTO;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.UserService;

/*
 * @author Shivendra Singh
 * 
 */

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<User> signUp(@RequestBody User user) {
		User createdUser = userService.signUp(user);
		return ResponseEntity.ok(createdUser);
	}

	@PostMapping("/signin")
	public ResponseEntity<?> signIn(@RequestBody UserDTO userDTO) {
		String email = userDTO.getEmail();
		String password = userDTO.getPassword();

		User user = userService.signIn(email, password);

		if (user != null) {
			return ResponseEntity.ok(user);
		} else {

			Optional<User> existingUser = userRepository.findByEmail(email);

			if (existingUser != null) {

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
			} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
		}
	}

}