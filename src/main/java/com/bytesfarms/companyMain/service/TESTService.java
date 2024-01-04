package com.bytesfarms.companyMain.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;

import jakarta.mail.MessagingException;

public interface TESTService {

	

	void createGoogleMeetEvent() throws IOException, GeneralSecurityException;

	String getAuthCode() throws MessagingException, IOException;

	String createMeeting();

}