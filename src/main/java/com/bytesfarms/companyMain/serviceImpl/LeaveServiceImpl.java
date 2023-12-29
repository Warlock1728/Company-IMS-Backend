package com.bytesfarms.companyMain.serviceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.dto.LeaveRequestDTO;
import com.bytesfarms.companyMain.entity.LeaveRequest;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.LeaveRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.LeaveService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	LeaveRepository leaveRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JavaMailSender javaMailSender;

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
			leaveRepository.save(leaveRequest);

			sendLeaveApplicationEmail(user);

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

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
	public List<LeaveRequestDTO> getAllLeavesForUser(Long userId) {
		List<LeaveRequest> leaves;

		if (userId == 0) {
			leaves = leaveRepository.findAll();
		} else {
			leaves = leaveRepository.findByUserId(userId);
		}

		return leaves.stream().map(this::convertToDTO).collect(Collectors.toList());
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

	private void sendLeaveApplicationEmail(User user) {

		List<String> hrAdminEmails = userRepository.findByRoleIdIn(Arrays.asList(1L, 2L)).stream().map(User::getEmail)
				.collect(Collectors.toList());

		if (!hrAdminEmails.isEmpty()) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(hrAdminEmails.toArray(new String[0]));
			message.setSubject("Leave Application Notification");
			message.setText("A leave has been applied by user: " + user.getUsername() + ". Please check.");

			javaMailSender.send(message);
		} else {

			System.out.println("No HR or Admin email addresses found.");
		}
	}

	private void sendLeaveStatusUpdateEmail(User user, String newStatus) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Leave Status Update Notification");
		message.setText("Your leave status has been updated to: " + newStatus + ". Please check.");

		javaMailSender.send(message);
	}

}
