package com.bytesfarms.companyMain.dto;



import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class TaskDTO {

	private Long id;
	private String taskDescription;

	private String expectedTime;
	private String actualTime;

	
    private String status;
	
	private Long userId;
}