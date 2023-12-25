package com.bytesfarms.companyMain.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

}
