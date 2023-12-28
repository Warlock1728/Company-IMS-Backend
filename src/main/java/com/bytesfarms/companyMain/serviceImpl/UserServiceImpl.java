package com.bytesfarms.companyMain.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.Role;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.entity.UserProfile;
import com.bytesfarms.companyMain.repository.RoleRepository;
import com.bytesfarms.companyMain.repository.UserProfileRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

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

			if (user.getProfile() != null) {
				UserProfile updatedProfile = user.getProfile();

				if (updatedProfile.getId() != null) {

					UserProfile existingProfile = userProfileRepository.findById(updatedProfile.getId())
							.orElseThrow(() -> new EntityNotFoundException(
									"UserProfile not found with id: " + updatedProfile.getId()));

					updateProfile(existingProfile, updatedProfile);
					existingUser.setProfile(existingProfile);
				} else {

					UserProfile newProfile = new UserProfile();
					updateProfile(newProfile, updatedProfile);
					userProfileRepository.save(newProfile);
					existingUser.setProfile(newProfile);
				}

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

	private void updateProfile(UserProfile existingProfile, UserProfile updatedProfile) {

		if (updatedProfile.getAddress() != null) {
			existingProfile.setAddress(updatedProfile.getAddress());
		}
		if (updatedProfile.getDob() != null) {
			existingProfile.setDob(updatedProfile.getDob());
		}
		if (updatedProfile.getAge() != null) {
			existingProfile.setAge(updatedProfile.getAge());
		}
		if (updatedProfile.getMobile() != null) {
			existingProfile.setMobile(updatedProfile.getMobile());
		}
		if (updatedProfile.getGender() != null) {
			existingProfile.setGender(updatedProfile.getGender());
		}
		if (updatedProfile.getMaritalStatus() != null) {
			existingProfile.setMaritalStatus(updatedProfile.getMaritalStatus());
		}
		if (updatedProfile.getDesignation() != null) {
			existingProfile.setDesignation(updatedProfile.getDesignation());
		}
		if (updatedProfile.getPhone() != null) {
			existingProfile.setPhone(updatedProfile.getPhone());
		}
		if (updatedProfile.getLocation() != null) {
			existingProfile.setLocation(updatedProfile.getLocation());
		}
		if (updatedProfile.getExperience() != null) {
			existingProfile.setExperience(updatedProfile.getExperience());
		}
		if (updatedProfile.getJoiningDate() != null) {
			existingProfile.setJoiningDate(updatedProfile.getJoiningDate());
		}

	}
}