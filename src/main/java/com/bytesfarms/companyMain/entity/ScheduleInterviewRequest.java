package com.bytesfarms.companyMain.entity;

import java.util.List;

import lombok.Data;

@Data
public class ScheduleInterviewRequest {
	private List<Long> applicationIds;
	private String interviewDateTime;
}
