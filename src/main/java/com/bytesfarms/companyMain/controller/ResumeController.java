package com.bytesfarms.companyMain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.service.ResumeService;

/*
 * @author Shivendra Singh
 * 
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

	@Autowired
	private ResumeService resumeService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadResume(@RequestParam("fileName") String fileName,
			@RequestPart("file") MultipartFile file, @RequestParam("jobPositionId") Long jobPositionId) {
		Long resumeId = resumeService.saveResume(fileName, file, jobPositionId);
		if (resumeId != null) {
			return new ResponseEntity<>("Resume uploaded successfully. Resume ID: " + resumeId, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed to upload resume.", HttpStatus.OK);
		}
	}
}