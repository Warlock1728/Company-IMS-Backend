package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

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

//import com.bytesfarms.companyMain.dto.ZoomMeetingObjectDTO;
import com.bytesfarms.companyMain.service.TESTService;
import com.bytesfarms.companyMain.util.IMSConstants;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp; // Correct import
//import com.google.api.client.extensions.java6.auth.oauth2.LocalServerReceiver; // Correct import
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

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

	private static final String APPLICATION_NAME = "Google Meet Scheduler";
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final String CREDENTIALS_FILE_PATH = "/Users/shiv/Downloads/bytewiseprojectcloud.json";

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

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

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
}