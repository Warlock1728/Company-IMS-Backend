package com.bytesfarms.companyMain.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JobApplicationDTO {
	private Long id;
	private String resume;
	private String coverLetter;
	private LocalDateTime applicationDate;
	private boolean shortlisted;
	private boolean interviewScheduled;
}
