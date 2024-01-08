package com.bytesfarms.companyMain.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.entity.MeetData;
//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.TESTService;
import com.google.api.services.calendar.model.Event;

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
	
	//selenium

	@GetMapping("/getauthcode")
	public ResponseEntity<String> getAuthCode() throws MessagingException, IOException {

		String response = zoomMeetingService.getAuthCode();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//able to create some event

	@PostMapping("/createMeetingTest")
	public Event createMeetingTest() throws IOException, GeneralSecurityException {
		Calendar calendar = Calendar.getInstance();
	    calendar.set(2024, Calendar.JANUARY, 8); // year, month (0-based), day
	    Date january6StartDate = calendar.getTime();// Replace with your desired start date
		Date endDate = new Date(january6StartDate.getTime() + 3600000); // 1 hour later

		String calendarId = "c9b81b95f714771a339346e5e64b8c026cf78dbcace401c1c2d10bf38072f8b6@group.calendar.google.com";
		String summary = "A test to create a meeting"; // Replace with your desired meeting summary

		return zoomMeetingService.createMeetingTest(calendarId, summary, january6StartDate, endDate);
	}
	
	
	//testing one more evening 7:09 pm 5 january
	@PostMapping("/createMeetingTestAgain")
    public ResponseEntity<String> createMeetingTestingAgain() {
        String meetingLink = zoomMeetingService.createMeetingSpace();
        return ResponseEntity.ok(meetingLink);
    }
	
	//testing OM'S CODE
	
	
	
	
	
	@PostMapping("/scheduleOM")
    public String scheduleMeet(@RequestBody MeetData meetData) throws IOException, GeneralSecurityException {
		return zoomMeetingService.scheduleMeetbyOM(meetData);
		
    }
	
	
	

}
