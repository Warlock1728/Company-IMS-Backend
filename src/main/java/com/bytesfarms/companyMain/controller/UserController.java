package com.bytesfarms.companyMain.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.dto.UserDTO;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

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
	
	@DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam Long userId) {
        boolean deleted = userService.deleteUser(userId);

        if (deleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
	
	@PostMapping("/forgotPassword")
	public String forgetPassword(@RequestParam("email") String email, HttpServletRequest request) {
		return userService.forgetPassword(email, request);
	}
	 
	
	@PutMapping("/updatePassword")
	public String updatePassword(@RequestParam("UUID") String uuid, @RequestParam String password) {
		return userService.updatePassword(uuid, password);
 
	}

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam Long userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);

        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @PostMapping("/verifyOTP")
	public ResponseEntity<Boolean> verifyOTP(@RequestBody User user,@RequestParam String otp) {
		boolean createdUser = userService.verifyOtp(user,otp);
		return ResponseEntity.ok(createdUser);
	}
    

    @GetMapping("/getEmployees")
    public ResponseEntity<List<User>> getEmployees() {
        List<User> employees = userService.getEmployees();
        return ResponseEntity.ok(employees);
    }
	
    
   

}
