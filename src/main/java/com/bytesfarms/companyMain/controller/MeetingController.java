package com.bytesfarms.companyMain.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.MeetingService;

@RestController
@RequestMapping("/scheduleMeet")
public class MeetingController {

	@Autowired
	private MeetingService meetingService;

	@PostMapping("/zoom")
	public String scheduleMeetingwithPython(@RequestParam String agenda,
			@RequestParam String startDateTime,
			@RequestParam String topic,
			@RequestParam List<String> listOfAttendee,
			@RequestParam String organizer,
            @RequestPart("resumePdf") MultipartFile resumePdf)
			throws IOException, GeneralSecurityException {
		return meetingService.scheduleMeetOnZoom(agenda,startDateTime,topic,listOfAttendee,organizer, resumePdf);

	}

}
