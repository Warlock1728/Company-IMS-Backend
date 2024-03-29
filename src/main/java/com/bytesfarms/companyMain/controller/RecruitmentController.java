package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.dto.JobPositionDTO;
import com.bytesfarms.companyMain.entity.JobPosition;
import com.bytesfarms.companyMain.service.JobPositionService;

/*
 * @author Shivendra Singh
 */

@RestController
@RequestMapping("/recruitment")
public class RecruitmentController {

	@Autowired
	private JobPositionService jobPositionService;

	@GetMapping("/positions")
	public List<JobPosition> getAllJobPositions() {
		return jobPositionService.getAllJobPositions();
	}

	@PostMapping("/positions/add")
	public ResponseEntity<String> createJobPosition(@RequestBody JobPositionDTO jobPositionDTO) {
		Long jobId = jobPositionService.createJobPosition(jobPositionDTO);
		return new ResponseEntity<>("Job position created with ID: " + jobId, HttpStatus.OK);
	}

	@PutMapping("/positions/update")
	public ResponseEntity<String> updateJobPosition(@RequestParam Long id, @RequestBody JobPositionDTO jobPositionDTO) {
		jobPositionService.updateJobPosition(id, jobPositionDTO);
		return new ResponseEntity<>("Job position with ID " + id + " updated successfully", HttpStatus.OK);
	}

	@DeleteMapping("/positions/delete")
	public ResponseEntity<String> deleteJobPosition(@RequestParam Long id) {
		jobPositionService.deleteJobPosition(id);
		return new ResponseEntity<>("Job position with ID " + id + " deleted successfully", HttpStatus.OK);
	}

	// Now for applicants to add , and for admins to review shortlist and schedule
	// interviews.

	@PutMapping("/positions/shortlist/applicant")
	public ResponseEntity<JobPositionDTO> shortlistCandidates(@RequestParam Long id,
			@RequestBody List<Long> applicationIds) {
		JobPositionDTO updatedJobPosition = jobPositionService.shortlistCandidates(id, applicationIds);
		return new ResponseEntity<>(updatedJobPosition, HttpStatus.OK);
	}

}
