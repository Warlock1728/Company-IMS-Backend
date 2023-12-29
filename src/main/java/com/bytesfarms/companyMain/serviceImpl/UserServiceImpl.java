package com.bytesfarms.companyMain.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.OtpInfo;
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

	@Autowired
	JavaMailSender javaMailSender;

	private Map<String, OtpInfo> otpStorage = new HashMap<>();
	private static final long OTP_EXPIRATION_TIME_MILLIS = 5 * 60 * 1000; // 5 minutes

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

		if (user.getRole().getRoleName().equals("Guest")) {
			String otp = generateOtp();
			sendOtpToEmail(user.getEmail(), otp);
			otpStorage.put(user.getEmail(), new OtpInfo(otp, System.currentTimeMillis()));
			user.setRole(existingRole);
			user.setEmail(user.getEmail());

			return user;
		}
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

	private String generateOtp() {
		Random random = new Random();
		int otp = 1000 + random.nextInt(9000);
		return String.valueOf(otp);
	}

	public void sendOtpToEmail(String email, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("OTP for Registration");
		message.setText("Your OTP for registration as a Guest to ByteWise Manager is: " + otp);

		javaMailSender.send(message);
	}

	public boolean verifyOtp(User user, String userEnteredOtp) {
		OtpInfo otpInfo = otpStorage.get(user.getEmail());

		if (otpInfo != null && otpInfo.isValid(OTP_EXPIRATION_TIME_MILLIS) && otpInfo.getOtp().equals(userEnteredOtp)) {

			Role guestRole = roleRepository.findByRoleName("Guest")
					.orElseThrow(() -> new IllegalArgumentException("Role not found: Guest"));

			User user1 = new User();
			user1.setEmail(user.getEmail());
			user1.setRole(guestRole);
			user1.setUsername(user.getUsername());
			String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
			user1.setPassword(hashedPassword);

			userRepository.save(user1);

			otpStorage.remove(user.getEmail());

			return true;
		} else {
			otpStorage.remove(user.getEmail());
			return false;
		}
	}
}