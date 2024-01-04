package com.bytesfarms.companyMain.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.TESTService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api")
public class TESTController {

	@Autowired
	private TESTService zoomMeetingService;

	@PostMapping("/zoom/create-meeting")
	public String createMeeting() {
		return zoomMeetingService.createMeeting();
	}

	@PostMapping("/teams/create-meeting")
	public String createGoogleMeetEvent() {
		try {
			zoomMeetingService.createGoogleMeetEvent();
			return "Google Meet event created successfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to create Google Meet event. Check the logs for details.";
		}
	}

	@GetMapping("/getauthcode")
	public ResponseEntity<String> getAuthCode() throws MessagingException, IOException {

		String response = zoomMeetingService.getAuthCode();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
