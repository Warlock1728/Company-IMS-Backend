package com.bytesfarms.companyMain.service;

import com.bytesfarms.companyMain.entity.User;

public interface UserService {
	User signUp(User user);

	User signIn(String email, String password);
}
