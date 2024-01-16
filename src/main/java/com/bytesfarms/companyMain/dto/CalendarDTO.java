package com.bytesfarms.companyMain.dto;

import java.time.LocalDateTime;
import java.util.List;

import biweekly.property.Attendee;
import biweekly.property.Organizer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CalendarDTO {
	private String subject;
	private String description;
	private String summary;
	private Organizer organizer;
	private String meetingLink;
	private LocalDateTime eventDateTime;
	
	
	 private List<Attendee> attendees;

}