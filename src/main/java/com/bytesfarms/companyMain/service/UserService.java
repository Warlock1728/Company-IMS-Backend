package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.entity.User;

public interface UserService {
	User signUp(User user);

	User signIn(String email, String password);

	boolean deleteUser(Long userId);

	User updateUser(Long userId, User user);

	List<User> getEmployees();
	boolean verifyOtp(User user, String otp) ;
}
