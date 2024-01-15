package com.bytesfarms.companyMain.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.dto.CalendarDTO;
import com.bytesfarms.companyMain.dto.EmailDTO;
import com.bytesfarms.companyMain.entity.MeetData;
//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.MeetingService;
import com.bytesfarms.companyMain.util.IMSConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Attendee;
import biweekly.property.Method;
import biweekly.property.Organizer;
import biweekly.util.Duration;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

/*
 * @author SHIVENDRA SINGH
 *
 */

@Service
public class MeetingServiceImpl implements MeetingService {

	private static final Logger log = LoggerFactory.getLogger(MeetingServiceImpl.class);

	@Value(IMSConstants.CLIENT_ID)
	private String client_id;

	@Value(IMSConstants.ACCOUNT_ID)
	private String account_id;

	@Value(IMSConstants.CLIENT_SECRET)
	private String client_secret;

	@Value(IMSConstants.AUTH_TOKEN_URL)
	private String auth_token_url;

	@Value(IMSConstants.MEETING_URL)
	private String api_meet_url;

	@Autowired
	private JavaMailSender javaMailSender;

	// ZOOM MEET Creation

	@Override
	public String scheduleMeetOnZoom(String agenda, String startDateTime, String topic,
			List<String> listOfAttendee,String organizer, MultipartFile resumePdf) {
		try {
			// Set up authentication

			String auth = client_id + ":" + client_secret;
			String base64Auth = Base64.getEncoder().encodeToString(auth.getBytes());

			Map<Object, Object> data = new HashMap<>();
			data.put("grant_type", "account_credentials");
			data.put("account_id", account_id);

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(auth_token_url))
					.header("Authorization", "Basic " + base64Auth)
					.header("Content-Type", "application/x-www-form-urlencoded").POST(buildFormDataFromMap(data))
					.build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Parse and print the access token
			String responseBody = response.body();
			System.out.println("Response: " + responseBody);

			String accessToken = responseBody.split("\"access_token\":")[1].split(",")[0].replaceAll("\"", "");

			log.info("Access Token IS FOUND : : " + accessToken);

			// call the api to schedule meet after getting token
			Map<String, Object> requestData = new HashMap<>();
			requestData.put("topic", topic);
			requestData.put("agenda", agenda);
			requestData.put("start_time", startDateTime);
			requestData.put("duration", "40");
			requestData.put("timezone", "IST");

			Map<String, Object> settings = new HashMap<>();
			settings.put("join_before_host", true);
			settings.put("host_video", false);
			settings.put("participant_video", true);
			settings.put("mute_upon_entry", true);
			settings.put("approval_type", 0);
			settings.put("audio", "both");
			settings.put("auto_recording", "cloud");

			requestData.put("settings", settings);

			HttpRequest meetRequest = HttpRequest.newBuilder().uri(URI.create(api_meet_url))
					.header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(requestData)))
					.build();

			HttpClient meetClient = HttpClient.newHttpClient();
			HttpResponse<String> meetResponse = meetClient.send(meetRequest, HttpResponse.BodyHandlers.ofString());

			String meetResponseBody = meetResponse.body();
			log.info("Meeting Response: " + meetResponseBody);

			String joinUrl = meetResponseBody.split("\"join_url\":")[1].split(",")[0].replaceAll("\"", "");

			log.info("Join URL: " + joinUrl);

//			sendCalendarInvitations(meetData.getListOfAttendee(), meetData.getTopic(), joinUrl,
//					meetData.getStartDateTime());

			// Trying to create calendar mail.
			EmailDTO emailDto = composeEmail(agenda, startDateTime, topic, listOfAttendee);
			CalendarDTO calenderDto = composeCalendar(agenda, startDateTime, topic,listOfAttendee, organizer,joinUrl);

			sendCalenderInvite(calenderDto, agenda, startDateTime, topic, listOfAttendee, resumePdf);

			return joinUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error came: " + e;
		}

	}
	
	
	
	
	// ALL THE HELPER METHODS FOR MEETING

	public void sendCalenderInvite(CalendarDTO calenderDto, String agenda, String startDateTime, String topic,
			 List<String> listOfAttendee, MultipartFile resumePdf)
			throws IOException, MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		mimeMessage.setRecipients(Message.RecipientType.TO, getToAddress(calenderDto.getAttendees()));
		mimeMessage.setSubject(calenderDto.getSubject());

		MimeMultipart mimeMultipart = new MimeMultipart("mixed");

		mimeMultipart.addBodyPart(getAttachmentMimeBodyPart(resumePdf));
		mimeMultipart.addBodyPart(createCalenderMimeBody(calenderDto));

		mimeMessage.setContent(mimeMultipart);
		javaMailSender.send(mimeMessage);

	}

	private MimeBodyPart getAttachmentMimeBodyPart(MultipartFile resumePdf) throws IOException, MessagingException {
		MimeBodyPart attachmentMimeBodyPart = new MimeBodyPart();
		attachmentMimeBodyPart
				.setDataHandler(new DataHandler(new ByteArrayDataSource(resumePdf.getBytes(), "application/pdf")));
		attachmentMimeBodyPart.setFileName("resume.pdf");

		return attachmentMimeBodyPart;
	}

	private BodyPart createCalenderMimeBody(CalendarDTO calenderDto) throws IOException, MessagingException {
		MimeBodyPart calenderBody = new MimeBodyPart();

		final DataSource source = new ByteArrayDataSource(createCal(calenderDto), "text/calender; charset=UTF-8");
		calenderBody.setDataHandler(new DataHandler(source));
		calenderBody.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");

		return calenderBody;
	}

	private Address[] getToAddress(List<Attendee> attendees) {
		return attendees.stream().map(attendee -> {
			Address address = null;
			try {
				address = new InternetAddress(attendee.getEmail());
			} catch (AddressException e) {
				e.printStackTrace();
			}
			return address;
		}).collect(Collectors.toList()).toArray(new InternetAddress[0]);
	}

	private EmailDTO composeEmail(String agenda, String startDateTime, String topic,
			List<String> listOfAttendee) {
		return EmailDTO.builder().from("managerbytewise@gmail.com")
				.message(String.format("Your interview is scheduled at %s", startDateTime)).subject("Interview")
				.toList(listOfAttendee).build();

	}

	private String createCal(CalendarDTO calenderDto) {
		ICalendar ical = new ICalendar();
		ical.addProperty(new Method(Method.REQUEST));
		ical.setUrl(calenderDto.getMeetingLink());

		VEvent event = new VEvent();
		event.setUrl(calenderDto.getMeetingLink());
		event.setSummary(calenderDto.getSummary());
		event.setDescription(calenderDto.getDescription());
		event.setDateStart(getStartDate(calenderDto.getEventDateTime()));
		event.setDuration(new Duration.Builder().hours(1).build());
		event.setOrganizer(calenderDto.getOrganizer());
		addAttendees(event, calenderDto.getAttendees());
		ical.addEvent(event);
		return Biweekly.write(ical).go();
	}

	private CalendarDTO composeCalendar(String agenda, String startDateTime, String topic,
			List<String> listOfAttendee, String organizer,String joinUrl) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		LocalDateTime eventDateTime = LocalDateTime.parse(startDateTime, formatter);
		List<Attendee> attendees = convertEmailsToAttendees(listOfAttendee);

		String subject = String.format("Dear Candidate, Your Interview is Scheduled with BytesFarms Technologies");
		String description = "You are invited to the first round of Technical Interview. Please make sure to join on time.";

		StringBuilder detailedDescription = new StringBuilder();
		detailedDescription.append("Join the meeting using the link below:\n").append(joinUrl).append("\n\n")
				.append("Meeting Details:\n").append("Topic: ").append(topic).append("\n").append("Agenda: ")
				.append(agenda).append("\n").append("Start Time: ").append(eventDateTime).append("\n")
				.append("Organizer: ").append("BytesFarms Technologies").append("\n");

		return CalendarDTO.builder().subject(subject).description(description)
				.description(detailedDescription.toString()).meetingLink(joinUrl)
				.summary("Scheduling your First round of Technical Interview").eventDateTime(eventDateTime)
				.organizer(new Organizer("BytesFarms Technologies",organizer))
				.attendees(attendees).build();
	}

	private List<Attendee> convertEmailsToAttendees(List<String> emailList) {
		List<Attendee> attendees = new ArrayList<>();

		for (String email : emailList) {
			Attendee attendee = new Attendee(email, email);

			attendees.add(attendee);
		}

		return attendees;
	}

	private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
		var builder = new StringBuilder();
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

	private void addAttendees(VEvent event, List<Attendee> attendees) {
		for (Attendee attendee : attendees) {
			event.addAttendee(attendee);
		}
	}

	private Date getStartDate(LocalDateTime eventDateTime) {
		Instant instant = eventDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

}