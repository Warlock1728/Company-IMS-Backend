package com.bytesfarms.companyMain.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.service.TimeSheetService;

@RestController
@RequestMapping("/timesheet")
public class TimeSheetController {

	@Autowired
	private TimeSheetService timeSheetService;

	@PostMapping("/checkin")
	public ResponseEntity<String> checkIn(@RequestParam Long userId) {
		timeSheetService.checkIn(userId);
		return new ResponseEntity<>("Checked in successfully", HttpStatus.OK);
	}

	@PostMapping("/checkout")
	public ResponseEntity<String> checkOut(@RequestParam Long userId) {
		timeSheetService.checkOut(userId);
		return new ResponseEntity<>("Checked out successfully", HttpStatus.OK);
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
	public ResponseEntity<String> getTotalHours(@RequestParam Long userId) {
		Duration totalWorkDuration = timeSheetService.calculateTotalHours(userId);
		long hours = totalWorkDuration.toHours();
		long minutes = totalWorkDuration.toMinutesPart();
		long seconds = totalWorkDuration.toSecondsPart();

		return new ResponseEntity<>(
				"Total Time worked: " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds",
				HttpStatus.OK);
	}

}