package com.bytesfarms.companyMain.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
	User signUp(User user);

	User signIn(String email, String password);

	boolean deleteUser(Long userId);

	User updateUser(Long userId, User user);

	List<User> getEmployees();
	boolean verifyOtp(User user, String otp) ;
	
	
	//For forgot and update password

	String forgetPassword(String email, HttpServletRequest request);

	String updatePassword(String uuid, String password);

	void saveImage(Long userId, MultipartFile image);
}
