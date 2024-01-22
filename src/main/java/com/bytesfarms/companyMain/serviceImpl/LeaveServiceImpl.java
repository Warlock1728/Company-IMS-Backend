package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.dto.LeaveRequestDTO;
import com.bytesfarms.companyMain.entity.LeaveRequest;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.LeaveRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.LeaveService;
import com.bytesfarms.companyMain.util.IMSConstants;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	LeaveRepository leaveRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	EmailSender emailSender;

	@Override
	public boolean applyLeave(LeaveRequestDTO leaveRequestDTO, Long userId) {

		try {
			LeaveRequest leaveRequest = new LeaveRequest();

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

			leaveRequest.setUser(user);
			leaveRequest.setLeaveType(leaveRequestDTO.getLeaveType());
			leaveRequest.setStartDate(leaveRequestDTO.getStartDate());
			leaveRequest.setEndDate(leaveRequestDTO.getEndDate());

			leaveRequest.setDescription(leaveRequestDTO.getDescription());
			leaveRequest.setStatus("Pending");

			String quarter = calculateQuarter(leaveRequestDTO.getStartDate());
			leaveRequest.setQuarter(quarter);

			updateLeaveBalance(leaveRequest);

			leaveRepository.save(leaveRequest);

			sendLeaveApplicationEmail(user, leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	private void updateLeaveBalance(LeaveRequest leaveRequest) {

		String leaveType = leaveRequest.getLeaveType();
		LocalDate startDate = leaveRequest.getStartDate();
		LocalDate endDate = leaveRequest.getEndDate();

		String quarter = calculateQuarter(leaveRequest.getStartDate());

		Optional<LeaveRequest> lastLeaveForQuarter = leaveRepository
				.findTopByUserIdAndQuarterOrderByStartDateDesc(leaveRequest.getUser().getId(), quarter);

		int leavesTaken = leaveRequest.getLeavesTaken();
		float availableLeaves = leaveRequest.getAvailableLeaves();
		float unpaidLeaves = leaveRequest.getLeaveWithoutPay();
		int halfDays = leaveRequest.getTotalHalfDay();

		if (lastLeaveForQuarter.isPresent()) {
			LeaveRequest lastLeave = lastLeaveForQuarter.get();

			availableLeaves = lastLeave.getAvailableLeaves();
			leavesTaken = lastLeave.getLeavesTaken();
			unpaidLeaves = lastLeave.getLeaveWithoutPay();
			halfDays = lastLeave.getTotalHalfDay();

		} else {
			// If no history is found then we set back the avl leaves to 3.
			availableLeaves = 3;
			leavesTaken = 0;
			unpaidLeaves = 0;
			halfDays = 0;
		}

		switch (leaveType) {
		case "Sick Leave":
			leavesTaken += 1;
			availableLeaves -= 1;
			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}
			break;
		case "Half Day":
			halfDays += 1;
			availableLeaves -= 0.5;

			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}

			break;

		case "Unplanned":
			leavesTaken += 1;
			availableLeaves -= 1;
			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}
			break;

		case "Planned":
			leavesTaken += 1;
			availableLeaves -= 1;
			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}
			break;

		case "Other":
			leavesTaken += 1;
			availableLeaves -= 1;
			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}
			break;

		}

		long daysDifference = ChronoUnit.DAYS.between(startDate, endDate);
		if (daysDifference > 1) {
			leavesTaken += (int) daysDifference - 1;
			availableLeaves = Math.max(-100, availableLeaves - (int) daysDifference + 1);
			if (availableLeaves <= 0) {
				unpaidLeaves += Math.abs(availableLeaves);
				availableLeaves = 0;
			}
		}

		// Set the updated values back to the leave request

		Log.info("Leaves taken are : " + leavesTaken);
		Log.info("Leaves available are : " + availableLeaves);

		leaveRequest.setLeavesTaken(leavesTaken);
		leaveRequest.setAvailableLeaves(availableLeaves);
		leaveRequest.setLeaveWithoutPay(unpaidLeaves);
		leaveRequest.setTotalHalfDay(halfDays);

	}

	private String calculateQuarter(LocalDate startDate) {
		int month = startDate.getMonthValue();
		if (month >= 1 && month <= 3) {
			return "Jan-Mar";
		} else if (month >= 4 && month <= 6) {
			return "Apr-Jun";
		} else if (month >= 7 && month <= 9) {
			return "Jul-Sep";
		} else {
			return "Oct-Dec";
		}
	}

//	private void updateQuarterData(LeaveRequest leaveRequest) {
//
//		String leaveType = leaveRequest.getLeaveType();
//		switch (leaveType) {
//		case "Full Day":
//			leaveRequest.setLeavesThisQuarter(leaveRequest.getLeavesThisQuarter() + 1);
//			break;
//		case "Half Day":
//			leaveRequest.setTotalHalfDay(leaveRequest.getTotalHalfDay() + 1);
//			if (leaveRequest.getTotalHalfDay() % 2 == 0) {
//				leaveRequest.setLeavesThisQuarter(leaveRequest.getLeavesThisQuarter() + 1);
//			}
//			break;
//		case "Leave Without Pay":
//			leaveRequest.setLeavesWithoutPay(leaveRequest.getLeavesWithoutPay() + 1);
//			break;
//
//		}
//	}

	@Override
	public boolean updateLeaveStatus(Long leaveRequestId, LeaveRequestDTO leaveRequestDTO) {

		try {
			LeaveRequest leaveRequest = leaveRepository.findById(leaveRequestId)

					.orElseThrow(
							() -> new EntityNotFoundException("Leave request not found with ID: " + leaveRequestId));

			String originalStatus = leaveRequest.getStatus();

			if (leaveRequestDTO.getStatus() != null) {
				leaveRequest.setStatus(leaveRequestDTO.getStatus());
			}

			if (leaveRequestDTO.getDescription() != null) {
				leaveRequest.setDescription(leaveRequestDTO.getDescription());
			}

			if (leaveRequestDTO.getLeaveType() != null) {
				leaveRequest.setLeaveType(leaveRequestDTO.getLeaveType());
			}

			if (leaveRequestDTO.getStartDate() != null) {
				leaveRequest.setStartDate(leaveRequestDTO.getStartDate());
			}

			if (leaveRequestDTO.getEndDate() != null) {
				leaveRequest.setEndDate(leaveRequestDTO.getEndDate());
			}

			leaveRepository.save(leaveRequest);

			if (!originalStatus.equals(leaveRequestDTO.getStatus())) {

				sendLeaveStatusUpdateEmail(leaveRequest.getUser(), leaveRequestDTO.getStatus());
			}

			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteLeaveRequest(Long leaveRequestId) {
		Optional<LeaveRequest> leaveRequestOptional = leaveRepository.findById(leaveRequestId);

		if (leaveRequestOptional.isPresent()) {
			leaveRepository.delete(leaveRequestOptional.get());
			return true;
		}

		return false;
	}

	@Override
	public List<LeaveRequest> getAllLeavesForUser(Long userId, String quarter) {
		List<LeaveRequest> leaves;

		if (userId == 0) {
			leaves = leaveRepository.findAll();
		} else {
			LocalDate filterDate;
			if (quarter != null && !quarter.isEmpty()) {

				filterDate = LocalDate.now();
				quarter = calculateQuarter(filterDate);
				return leaveRepository.findByUserIdAndQuarter(userId, quarter);
			} else {

				return leaveRepository.findByUserId(userId);
			}
		}

		return leaves;
	}

	private LeaveRequestDTO convertToDTO(LeaveRequest leave) {
		LeaveRequestDTO leaveDTO = new LeaveRequestDTO();
		leaveDTO.setLeaveType(leave.getLeaveType());
		leaveDTO.setStartDate(leave.getStartDate());
		leaveDTO.setEndDate(leave.getEndDate());
		leaveDTO.setUser(leave.getUser());
		leaveDTO.setDescription(leave.getDescription());
		leaveDTO.setStatus(leave.getStatus());
		leaveDTO.setId(leave.getId());

		return leaveDTO;
	}

	private void sendLeaveApplicationEmail(User user, LocalDate startDate, LocalDate endDate)
			throws AddressException, MessagingException {

		List<String> hrAdminEmails = userRepository.findByRoleIdIn(Arrays.asList(1L, 2L)).stream().map(User::getEmail)
				.collect(Collectors.toList());

		if (!hrAdminEmails.isEmpty()) {

			HashMap<String, String> map = new HashMap<>();
			map.put("userName", user.getUsername());
			map.put("startDate", startDate.toString());
			map.put("endDate", endDate.toString());

			String subject = "Leave Application Notification";

			String emailTemplate = loadHtmlTemplate("/Leave-Notification-Admin.html");

			emailSender.sendEmail(IMSConstants.RECEIPIENT, emailTemplate, subject, map);

			// Code to send dynamic mails
//
//			for (String email : hrAdminEmails) {
//				emailSender.sendEmail(email, emailTemplate, subject, map);
//			}
		} else {

			System.out.println("No HR or Admin email addresses found.");
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

	private void sendLeaveStatusUpdateEmail(User user, String newStatus) throws AddressException, MessagingException {

		String subject = "We have an update on your Leave Notification !";
		String emailTemplate = loadHtmlTemplate("/Leave-Status-Update.html");

		HashMap<String, String> map = new HashMap<>();
		map.put("newStatus", newStatus);
		map.put("userName", user.getUsername());
		emailSender.sendEmail(IMSConstants.RECEIPIENT, emailTemplate, subject, map);

		// emailSender.sendEmail(user.getEmail(), emailTemplate, subject, map);

	}

}
