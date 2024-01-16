package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.OtpInfo;
import com.bytesfarms.companyMain.entity.Role;
import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.entity.UserProfile;
import com.bytesfarms.companyMain.repository.RoleRepository;
import com.bytesfarms.companyMain.repository.TimeSheetRepository;
import com.bytesfarms.companyMain.repository.UserProfileRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.UserService;
import com.bytesfarms.companyMain.util.IMSConstants;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/*
 * @author SS
 */

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private TimeSheetRepository timeSheetRepository;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	EmailSender emailSender;

	@Autowired
	IMSConstants imsConstants;

	private static final String SECRET_KEY = IMSConstants.SECRET_KEY;

	private Map<String, OtpInfo> otpStorage = new HashMap<>();
	private static final long OTP_EXPIRATION_TIME_MILLIS = 5 * 60 * 1000; // 5 minutes

	@Override
	public User signUp(User user) throws IOException, MessagingException {
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

			if (user.getFixedSalary() != null) {
				String salary = user.getFixedSalary();

				String encryptedSalary = encrypt(salary);

				existingUser.setFixedSalary(encryptedSalary);
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

	// Method to encrypt
	private byte[] deriveKey(String secret) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String encrypt(String salary) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] derivedKey = deriveKey(SECRET_KEY);
			SecretKey secretKey = new SecretKeySpec(derivedKey, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(salary.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<User> getEmployees() {
		List<User> employees = userRepository.findByRoleIdIn(Arrays.asList(2L, 3L)); // Fetch both HR and employees

		// Iterate through each employee and check if they have checked in today
		for (User employee : employees) {
			boolean isCheckedInToday = hasCheckedInToday(employee, timeSheetRepository);
			employee.setCheckedInToday(isCheckedInToday);
		}

		return employees;
	}

	private boolean hasCheckedInToday(User employee, TimeSheetRepository timeSheetRepository) {
		LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

		// Retrieve time sheets for today using the repository method
		List<TimeSheet> todayTimeSheets = timeSheetRepository.findTodayTimeSheetByUserId(employee.getId(), today,
				today.plusDays(1));

		return !todayTimeSheets.isEmpty();
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

	public void sendOtpToEmail(String email, String otp) throws IOException, MessagingException {

		InputStream logoInputStream = getClass().getResourceAsStream("/BytewiseLogo.png");
		byte[] logoBytes = IOUtils.toByteArray(logoInputStream);
		String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);

		String emailTemplate = loadHtmlTemplate("/OTP.html");
		String subject = "Your OTP to Sign in to ByteWise Manager";

		HashMap<String, String> map = new HashMap<>();
		map.put("BytewiseLogo", logoBase64);
		map.put("OTP", otp);

		// Log.info("This is template : : "+ emailTemplate);
		Log.info("These are context map objects : : " + map);

		try {

			emailSender.sendEmail(email, emailTemplate, subject, map);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private String loadHtmlTemplate(String templatePath) {
		try (InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
				Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			return scanner.useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new RuntimeException("Error loading HTML template", e);
		}
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

	@Override
	public String forgetPassword(String email, HttpServletRequest request) {
		String uuid = UUID.randomUUID().toString();

		// Set the expiration time
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		Date expirationTime = calendar.getTime();

		User existingUser = userRepository.findUserByEmail(email);
		if (existingUser != null) {
			existingUser.setUuid(uuid);
			existingUser.setResetTokenExpiration(expirationTime);

			HashMap<String, String> map = new HashMap<>();
			String fullUrl = request.getRequestURL().toString();
			try {
				URL url = new URL(fullUrl);
				String host = url.getHost();
				String scheme = request.getScheme();
				// String link = scheme + "://" + host + "/updatepassword?token=" + uuid;
				String link = "http://localhost:3000/updatepassword?token=" + uuid;

				map.put("RESET_LINK", link);
				String emailTemplate = loadHtmlTemplate("/ForgotPassword.html");
				String subject = "Forgot Your Password ? ";

				emailSender.sendEmail(email, emailTemplate, subject, map);

			} catch (Exception e) {
				e.printStackTrace();
			}
			userRepository.save(existingUser);// saving user
			return uuid;
		}
		return "Email not found";
	}

	public String updatePassword(String uuid, String password) {
		User user = userRepository.findByUuid(uuid);
		if (user != null) {
			// Check if the token has expired
			Date expirationTime = user.getResetTokenExpiration();
			Date currentTime = new Date();
			if (expirationTime != null && expirationTime.after(currentTime)) {
				// Token is not expired, allow password update
				String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
				user.setPassword(hashedPassword);
				user.setUuid(null);
				userRepository.save(user);
				return "Password Updated Successfully";
			} else {

				return "Expiration time reached";
			}
		} else {
			return "User with uuid isn't found in database";
		}
	}

	@Override
	public void saveImage(Long userId, MultipartFile image) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		try {
			user.setImage(image.getBytes());
			userRepository.save(user);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save image for user with id: " + userId, e);
		}
	}
}