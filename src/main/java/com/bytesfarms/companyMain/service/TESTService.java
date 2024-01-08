package com.bytesfarms.companyMain.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.entity.MeetData;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;

import jakarta.mail.MessagingException;

public interface TESTService {

	void createGoogleMeetEvent() throws IOException, GeneralSecurityException;

	String getAuthCode() throws MessagingException, IOException;

	String createMeeting();

	Event createMeetingTest(String calendarId, String summary, Date startDate, Date endDate)
			throws IOException, GeneralSecurityException;

	String createMeetingSpace();

	String scheduleMeetbyOM(MeetData meetData) throws IOException, NumberFormatException, GeneralSecurityException;

	

	Event createEvent(String summary, Date startDate, Date endDate);

}