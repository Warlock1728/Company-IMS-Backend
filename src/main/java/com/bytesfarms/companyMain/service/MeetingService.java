package com.bytesfarms.companyMain.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.MeetData;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;

import jakarta.mail.MessagingException;

public interface MeetingService {

	String scheduleMeetOnZoom(String agenda, String startDateTime, String topic,
			List<String> listOfAttendee, String organizer, MultipartFile resumePdf);

}