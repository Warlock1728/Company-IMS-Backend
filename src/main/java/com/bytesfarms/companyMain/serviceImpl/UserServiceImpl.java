package com.bytesfarms.companyMain.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.Role;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.RoleRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User signUp(User user) {
		if (user == null || user.getUsername() == null || user.getEmail() == null || user.getPassword() == null
				|| user.getRole() == null || user.getRole().getRoleName() == null) {
			throw new IllegalArgumentException("All fields are required for user registration");
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
		}

		if (userRepository.existsByUsername(user.getUsername())) {
			throw new IllegalArgumentException("Username is already in use: " + user.getUsername());
		}

		Role existingRole = roleRepository.findByRoleName(user.getRole().getRoleName())
				.orElseThrow(() -> new IllegalArgumentException("Role not found: " + user.getRole().getRoleName()));

		user.setRole(existingRole);

		String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(hashedPassword);

		return userRepository.save(user);
	}

	@Override
	public User signIn(String email, String password) {

		Optional<User> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			if (BCrypt.checkpw(password, user.getPassword())) {
				return user;
			}
		}

		return null;
	}

	@Override
	public boolean deleteUser(Long userId) {
		try {
			userRepository.deleteById(userId);
			return true;
		} catch (EmptyResultDataAccessException ex) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public User updateUser(Long userId, User user) {
		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			if (user.getUsername() != null) {
				existingUser.setUsername(user.getUsername());
			}
			if (user.getEmail() != null) {
				existingUser.setEmail(user.getEmail());
			}
			if (user.getPassword() != null) {
				existingUser.setPassword(user.getPassword());
			}
			if (user.getRole() != null) {
				existingUser.setRole(user.getRole());
			}

			return userRepository.save(existingUser);
		} else {
			return null;
		}
	}

	@Override
	public List<User> getEmployees() {
		return userRepository.findByRole_Id(3); // Method to get all employees for IMS Dashboard
	}
}