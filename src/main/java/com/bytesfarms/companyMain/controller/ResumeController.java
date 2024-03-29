package com.bytesfarms.companyMain.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.Resume;
import com.bytesfarms.companyMain.service.ResumeService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

/*
 * @author Shivendra Singh
 * 
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

	@Autowired
	private ResumeService resumeService;

	// Apply to a job position

	@PostMapping("/upload")
	public ResponseEntity<String> uploadResume(@RequestPart("file") MultipartFile file,
			@RequestParam("jobPositionId") Long jobPositionId, @RequestParam("userId") Long userId,
			@RequestParam("lastJobTitle") String lastJobTitle,
			@RequestParam("lastJobExperience") Integer lastJobExperience,
			@RequestParam("lastJobCompany") String lastJobCompany,
			@RequestParam("expectedSalary") BigDecimal expectedSalary) throws AddressException, MessagingException {
		Long resumeId = resumeService.saveResume(file, jobPositionId, userId, lastJobTitle, lastJobExperience,
				lastJobCompany, expectedSalary);
		if (resumeId != null) {
			return new ResponseEntity<>("Resume uploaded successfully. Resume ID: " + resumeId, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed to upload resume.", HttpStatus.OK);
		}
	}

	// Get all the open job applications

	@GetMapping("/get")
	public ResponseEntity<List<Resume>> getResumesByJobPositionId(@RequestParam Long jobPositionId) {
		List<Resume> resumes = resumeService.getResumesByJobPositionId(jobPositionId);
		return new ResponseEntity<>(resumes, HttpStatus.OK);
	}

	@PutMapping("/update-status")
	public ResponseEntity<String> updateResumeStatus(@RequestParam Long resumeId, @RequestBody String status,
			@RequestParam Long jobPositionId) throws AddressException, MessagingException {
		boolean isUpdated = resumeService.updateResumeStatus(resumeId, status, jobPositionId);

		if (isUpdated) {
			return new ResponseEntity<>("Resume status updated successfully.", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed to update resume status.", HttpStatus.OK);
		}
	}
}