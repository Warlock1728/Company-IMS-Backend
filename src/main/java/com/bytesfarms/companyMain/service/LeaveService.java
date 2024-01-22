package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.dto.LeaveRequestDTO;
import com.bytesfarms.companyMain.entity.LeaveRequest;

public interface LeaveService {

	boolean applyLeave(LeaveRequestDTO leaveRequestDTO, Long userId);

	boolean updateLeaveStatus(Long leaveRequestId, LeaveRequestDTO leaveRequestDTO);

	boolean deleteLeaveRequest(Long leaveRequestId);

	List<LeaveRequest> getAllLeavesForUser(Long userId,String quarter);

}
