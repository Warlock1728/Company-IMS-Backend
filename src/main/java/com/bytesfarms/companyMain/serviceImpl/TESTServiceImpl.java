package com.bytesfarms.companyMain.serviceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.bytesfarms.companyMain.entity.MeetData;
//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.TESTService;
import com.bytesfarms.companyMain.util.IMSConstants;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp; // Correct import
//import com.google.api.client.extensions.java6.auth.oauth2.LocalServerReceiver; // Correct import
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
//import com.google.apps.meet.v2beta.Space;
//import com.google.apps.meet.v2beta.SpacesServiceClient;
import com.google.api.services.calendar.model.EventReminder;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.mail.MessagingException;

@Service
public class TESTServiceImpl implements TESTService {

	private static final Logger log = LoggerFactory.getLogger(TESTServiceImpl.class);

	@Value(IMSConstants.CLIENT_ID)
	private String zoomApiKey;

	@Value(IMSConstants.CLIENT_SECRET)
	private String zoomApiSecret;

	@Value(IMSConstants.ZOOM_USER_ID)
	private String zoomUserId;

	@Value(IMSConstants.ZOOM_USER_PASSWORD)
	private String zoomPassword;

	// ZOOM MEET TEST

	@Override
	public String createMeeting() {
		log.debug("Request to create a Zoom meeting");

//		// Assuming you have a ZoomAPI class with createMeeting method
//		ZoomAPI zoomAPI = new ZoomAPI(zoomApiKey, zoomApiSecret);
//
//		// Create a ZoomMeetingRequest object.
//		ZoomMeetingRequest meetingRequest = new ZoomMeetingRequest();
//
//		// Set the meeting topic.
//		meetingRequest.setTopic("My Meeting");
//
//		// Set the meeting start time.
//		meetingRequest.setStartDateTime(new Date());
//
//		// Set the meeting duration.
//		meetingRequest.setDuration(60);
//
//		// Create the meeting.
//		ZoomMeetingResponse meetingResponse = zoomAPI.createMeeting(zoomUserId, meetingRequest);
//
//		// Get the meeting ID.
//		String meetingId = meetingResponse.getId();
//
//		// Print the meeting ID.
//		System.out.println("Meeting ID: " + meetingId);
//
//		// You might want to return or use the meeting ID in your DTO
//		zoomMeetingObjectDTO.setMeetingId(meetingId);

		return null;
	}

	// Testing google meet scheduler....

	private static final String APPLICATION_NAME = "Google Meet Scheduler";
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final String CREDENTIALS_FILE_PATH = "/Users/shiv/Downloads/bytewiseprojectcloud.json";
	private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

	private static final String CREDENTIALS_FILE_PATH_OAUTH = "/bytewisemanagerOauth.json";

	@Override
	public void createGoogleMeetEvent() throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME)
				.build();

		Event event = new Event().setSummary("Google Meet Event").setDescription("This is a Google Meet event");

		DateTime startDateTime = new DateTime("2024-01-01T10:00:00.000Z");
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC");
		event.setStart(start);

		DateTime endDateTime = new DateTime("2024-01-01T11:00:00.000Z");
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC");
		event.setEnd(end);

		EventAttendee[] attendees = new EventAttendee[] {
				new EventAttendee().setEmail("shivendrasinghbais14@gmail.com"),
				new EventAttendee().setEmail("shivendra.bais@bytesfarms.com")
				// Add more participants as needed
		};
		event.setAttendees(Arrays.asList(attendees));

		String calendarId = "primary"; // Use "primary" for the user's primary calendar
		event = service.events().insert(calendarId, event).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		try (Reader reader = new InputStreamReader(Objects.requireNonNull(
				TESTServiceImpl.class.getClassLoader().getResourceAsStream("bytewiseprojectcloud.json")))) {
			log.info("Credentials file loaded successfully.");

			StringBuilder content = new StringBuilder();
			int data;
			while ((data = reader.read()) != -1) {
				content.append((char) data);
			}
			log.info("JSON File Content: " + content.toString());

			StringReader stringReader = new StringReader(content.toString());

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, stringReader);

			log.info("Client Secrets: " + clientSecrets.toPrettyString());

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					clientSecrets, Collections.singletonList(CalendarScopes.CALENDAR))
					.setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(
							new java.io.File(TOKENS_DIRECTORY_PATH)))
					.setAccessType("offline").build();

			return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		} catch (IOException e) {
			log.error("Error loading credentials file", e);
			throw e;
		}
	}

	@Override
	public String getAuthCode() throws MessagingException, IOException {
//		System.setProperty("webdriver.chrome.driver", " /opt/homebrew/bin/chromedriver");

		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get(
					"https://zoom.us/oauth/authorize?response_type=code&client_id=rIFk56kMT_6F5G54NvzXKA&redirect_uri=https://oauth.pstmn.io/v1/callback");

			// Perform login (replace these with your Zoom account credentials)
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
			WebElement passwordInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
			WebElement signInButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("js_btn_login")));

			emailInput.sendKeys("shivendrasinghbais14@gmail.com");
			passwordInput.sendKeys("Shivendra@1716");
			signInButton.click();

//			WebElement otpInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("otp_field")));
//
//			String otpFromEmail = getOTPFromEmail("shivendrasinghbais14@gmail.com", "Shivendra@1728",
//					"Code for signing in to Zoom"); // Replace with your actual OTP
//			otpInput.sendKeys(otpFromEmail);
//
//			// Submit the OTP form
//			WebElement submitButton = driver.findElement(By.id("submit_btn"));
//			submitButton.click();

			// Wait for the redirect to the callback URL
			wait.until(ExpectedConditions.urlContains("https://oauth.pstmn.io/v1/callback"));

			// Extract the authorization code from the redirected URL
			String currentUrl = driver.getCurrentUrl();
			String authorizationCode = extractCodeFromUrl(currentUrl);

			System.out.println("Authorization Code: " + authorizationCode);
			return authorizationCode;
		} finally {
			driver.quit();

		}
	}

	private static String extractCodeFromUrl(String authorizationUrl) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(authorizationUrl);
		String code = builder.buildAndExpand().getQueryParams().getFirst("code");
		return code;
	}

	// Testing a api to create meet

	@Override
	public Event createMeetingTest(String calendarId, String summary, Date startDate, Date endDate)
			throws IOException, GeneralSecurityException {
		Calendar service = getCalendarService();

		Event event = createEvent(summary, startDate, endDate);

		// Insert the event
		Event createdEvent = service.events().insert(calendarId, event).execute();
		System.out.println("Event created: " + createdEvent.getHtmlLink());

		// Make the event public
		createdEvent = makeEventPublic(service, calendarId, createdEvent.getId());

		// Add conference link and update the event
		createdEvent = addConferenceLink(service, calendarId, createdEvent);

		// Send invitations to attendees
//		List<String> attendeeEmails = Arrays.asList("shivendrasinghbais14@gmail.com", "shivendra.bais@bytesfarms.com");
//		sendInvitations(service, calendarId, createdEvent.getId(), attendeeEmails);

		return createdEvent;
	}

	@Override
	public Event createEvent(String summary, Date startDate, Date endDate) {
		Event event = new Event().setSummary(summary).setDescription("A Google Meet event to Test");

		DateTime startDateTime = new DateTime(startDate);
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC");
		event.setStart(start);

		DateTime endDateTime = new DateTime(endDate);
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC");
		event.setEnd(end);
		event.setGuestsCanInviteOthers(true);
		
		//Commented this code as Domain wide allegation needs to be approved and i dont have it .

//		List<String> attendeeEmails = Arrays.asList("shivendra.bais@bytesfarms.com", "shivendrasinghbais14@gmail.com");
//
//		List<EventAttendee> attendees = new ArrayList<>();
//		for (String attendeeEmail : attendeeEmails) {
//			EventAttendee attendee = new EventAttendee().setEmail(attendeeEmail);
//			attendees.add(attendee);
//		}
//
//		event.setAttendees(attendees);
		
		
		

		return event;
	}

//	private void sendInvitations(Calendar service, String calendarId, String eventId, List<String> attendeeEmails)
//			throws IOException {
//		Event event = service.events().get(calendarId, eventId).execute();
//
//		if (event.getAttendees() == null) {
//			event.setAttendees(new ArrayList<>());
//		}
//
//		for (String attendeeEmail : attendeeEmails) {
//			EventAttendee attendee = new EventAttendee().setEmail(attendeeEmail);
//			event.getAttendees().add(attendee);
//		}
//
//		service.events().update(calendarId, eventId, event).execute();
//	}

	private Event addConferenceLink(Calendar service, String calendarId, Event event) throws IOException {

		ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");

		CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
				.setConferenceSolutionKey(conferenceSolutionKey);

		ConferenceData conferenceData = new ConferenceData().setCreateRequest(createConferenceRequest);

		event.setConferenceData(conferenceData);
		

		log.info("This is conference data :: " + conferenceData); // Patch the event to add the conference link
		Event updatedEvent = service.events().update(calendarId, event.getId(), event).setConferenceDataVersion(1)
				.execute();

		log.info("This is updated event : : " + updatedEvent);
		// Return the updated event
		return updatedEvent;
	}

	private Calendar getCalendarService() throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentialsTest(HTTP_TRANSPORT))
				.setApplicationName("ByteWise Manager").build();
	}

	private Credential getCredentialsTest(final NetHttpTransport HTTP_TRANSPORT)
			throws IOException, GeneralSecurityException {
		InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("bytewiseprojectcloud.json");

		if (credentialsStream == null) {
			throw new IOException("Could not read credentials file");
		}

		GoogleCredential credentials = GoogleCredential.fromStream(credentialsStream, HTTP_TRANSPORT, JSON_FACTORY)
				.createScoped(Collections.singletonList(CalendarScopes.CALENDAR));

		credentialsStream.close();

		return credentials;
	}

	private Event makeEventPublic(Calendar service, String calendarId, String eventId) throws IOException {
		Event event = new Event().setVisibility("public");

		// Perform a patch operation to update the event
		return service.events().patch(calendarId, eventId, event).execute();
	}

	@Override
	public String createMeetingSpace() {
//        try (SpacesServiceClient spacesServiceClient = SpacesServiceClient.create()) {
//            // Assuming Space.newBuilder().build() is your default space configuration
//            Space space = Space.newBuilder().build();
//            Space response = spacesServiceClient.createSpace(space);
//
//            // Assuming there's a method to extract the meeting link from the response
//            String meetingLink = extractMeetingLink(response);
//
//            return meetingLink;
//        } catch (Exception e) {
//            // Handle exception appropriately
//            e.printStackTrace();
		return null;
//        }
//    }x

	}

//-------------------------------------------------------------------------------------------------------------------------------------	

	// Testing Freind'S CODE

	@Override
	public String scheduleMeetbyOM(MeetData meetData)
			throws IOException, NumberFormatException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentialsForOm(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();

		CreateConferenceRequest request = new CreateConferenceRequest();
		request.setRequestId(System.currentTimeMillis() + "-" + UUID.randomUUID())
				.setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"));
		// Create the event with the adjusted start and end times
		Event event = new Event().setSummary(meetData.getSummary())

				.setDescription(meetData.getDescription())
				.setConferenceData(new ConferenceData().setCreateRequest(request));

		// Create the start date and time in IST (10:00 AM)
		ZonedDateTime startISTDateTime = ZonedDateTime.of(Integer.parseInt(meetData.getStartDateTime().split("-")[0]),
				Integer.parseInt(meetData.getStartDateTime().split("-")[1]),
				Integer.parseInt(meetData.getStartDateTime().split("-")[2]),
				Integer.parseInt(meetData.getStartDateTime().split("-")[3]),
				Integer.parseInt(meetData.getStartDateTime().split("-")[4]), 0, 0, ZoneId.of("Asia/Kolkata"));
		DateTime startDateTime = new DateTime(startISTDateTime.toInstant().toEpochMilli());
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Kolkata");
		event.setStart(start);

		// Create the end date and time in IST (10:30 AM)
		ZonedDateTime endISTDateTime = ZonedDateTime.of(Integer.parseInt(meetData.getEndDateTime().split("-")[0]),
				Integer.parseInt(meetData.getEndDateTime().split("-")[1]),
				Integer.parseInt(meetData.getEndDateTime().split("-")[2]),
				Integer.parseInt(meetData.getEndDateTime().split("-")[3]),
				Integer.parseInt(meetData.getEndDateTime().split("-")[4]), 0, 0, ZoneId.of("Asia/Kolkata"));
		DateTime endDateTime = new DateTime(endISTDateTime.toInstant().toEpochMilli());
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Kolkata");
		event.setEnd(end);

		String[] recurrence = new String[] { "RRULE:FREQ=DAILY;COUNT=2" };
		event.setRecurrence(Arrays.asList(recurrence));

		List<EventAttendee> attendees = new ArrayList<>();
		for (String email : meetData.getListOfAttendee()) {
			attendees.add(new EventAttendee().setEmail(email));
		}
		event.setAttendees(attendees);

		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(24 * 60),
				new EventReminder().setMethod("popup").setMinutes(10), };
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);

		String calendarId = "primary";
		event = service.events().insert(calendarId, event).execute();
		event = service.events().insert(calendarId, event).setConferenceDataVersion(1).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
		return "This is event data" + event;
	}

	private static Credential getCredentialsForOm(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = TESTServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH_OAUTH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH_OAUTH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		// returns an authorized Credential object.
		return credential;
	}
	
	
	
	
}