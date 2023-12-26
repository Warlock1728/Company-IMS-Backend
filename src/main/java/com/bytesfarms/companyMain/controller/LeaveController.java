package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.dto.LeaveRequestDTO;
import com.bytesfarms.companyMain.service.LeaveService;

@RestController
@RequestMapping("/leave")
public class LeaveController {

	@Autowired
	private LeaveService leaveService;

	@PostMapping("/apply")
	public ResponseEntity<String> applyForLeave(@RequestBody LeaveRequestDTO leaveRequestDTO,
			@RequestParam Long userId) {
		boolean applied = leaveService.applyLeave(leaveRequestDTO, userId);
		if (applied) {
			return ResponseEntity.ok("Leave applied successfully.");
		} else {
			return ResponseEntity.status(200).body("Failed to apply leave. Please try again.");
		}
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateLeaveStatus(@RequestParam Long leaveRequestId,
			@RequestBody LeaveRequestDTO leaveRequestDTO) {

		boolean updated = leaveService.updateLeaveStatus(leaveRequestId, leaveRequestDTO);
		if (updated) {
			return ResponseEntity.ok("Leave updated successfully.");
		} else {
			return ResponseEntity.status(200).body("Failed to update leave status. Please try again.");
		}
	}
	@DeleteMapping("/delete")
    public ResponseEntity<String> deleteLeaveRequest(@RequestParam Long leaveRequestId) {
        boolean deleted = leaveService.deleteLeaveRequest(leaveRequestId);

        if (deleted) {
            return ResponseEntity.ok("Leave request deleted successfully.");
        } else {
            return ResponseEntity.status(200).body("Leave request not found.");
        }
    }
	
	 @GetMapping("/get")
	    public ResponseEntity<List<LeaveRequestDTO>> getAllLeavesForUser(@RequestParam Long userId) {
	        List<LeaveRequestDTO> leaves = leaveService.getAllLeavesForUser(userId);
	        return ResponseEntity.ok(leaves);
	    }

}