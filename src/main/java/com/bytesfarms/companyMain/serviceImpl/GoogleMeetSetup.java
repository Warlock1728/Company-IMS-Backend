package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

public class GoogleMeetSetup {

	public static void main(String[] args) throws IOException, GeneralSecurityException {

		System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "src/main/resources/bytewiseprojectcloud.json");

		// Create a Google Cloud project and enable the Google Calendar API.
		GoogleCredential credential = GoogleCredential.getApplicationDefault();
		HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		Calendar calendar = new Calendar.Builder(transport, GsonFactory.getDefaultInstance(), credential)
				.setApplicationName("Google Meet Setup").build();

		// Create a Google Calendar event and set the "Hangouts Meet" option to "Yes".
		Event event = new Event();
		event.setSummary("Google Meet Setup");
		event.setStart(null);
		event.setEnd(null);

		// Get the Google Calendar event's ID.
		String eventId = calendar.events().insert("primary", event).execute().getId();

		// Use the Google Calendar API to create a Google Meet link for the event.
		String meetLink = calendar.events().get("primary", eventId).execute().getHangoutLink();

		event.setHangoutLink(meetLink);

		// Use the Google Meet link to join the meeting.
		System.out.println("Google Meet link: " + meetLink);
	}
}