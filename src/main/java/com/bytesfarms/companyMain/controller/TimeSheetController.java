package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.service.TimeSheetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * @author Shivendra Singh
 */

@RestController
@RequestMapping("/timesheet")
public class TimeSheetController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TimeSheetService timeSheetService;

	@PostMapping("/checkin")
	public String checkIn(@RequestParam Long userId) {
		return timeSheetService.checkIn(userId);
		
	}

	@PostMapping("/checkout")
	public String checkOut(@RequestParam Long userId) {
		return timeSheetService.checkOut(userId);
		
	}

	@PostMapping("/break/start")
	public ResponseEntity<String> startLunchBreak(@RequestParam Long userId) {
		timeSheetService.startBreak(userId);
		return new ResponseEntity<>("Break started successfully", HttpStatus.OK);
	}

	@PostMapping("/break/end")
	public ResponseEntity<String> endLunchBreak(@RequestParam Long userId) {
		timeSheetService.endBreak(userId);
		return new ResponseEntity<>("Break ended successfully", HttpStatus.OK);
	}

	@GetMapping("/totalhours")
	public ResponseEntity<List<TimeSheet>> getTotalHoursData(@RequestParam Long userId) {
		List<TimeSheet> timeSheets = timeSheetService.calculateTotalHours(userId);
		return ResponseEntity.ok(timeSheets);
	}

}