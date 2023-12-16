package com.bytesfarms.companyMain.dto;

import java.util.List;

import lombok.Data;

@Data
public class JobPositionDTO {
	private String title;
	private String openings;
	private String experience;
	private String requirements;
	
	private List<JobApplicationDTO> applications;
	
}
