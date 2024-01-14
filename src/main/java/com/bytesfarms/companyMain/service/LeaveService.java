package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.dto.LeaveRequestDTO;

public interface LeaveService {

	boolean applyLeave(LeaveRequestDTO leaveRequestDTO, Long userId);

	boolean updateLeaveStatus(Long leaveRequestId, LeaveRequestDTO leaveRequestDTO);

	boolean deleteLeaveRequest(Long leaveRequestId);

	List<LeaveRequestDTO> getAllLeavesForUser(Long userId);

}
