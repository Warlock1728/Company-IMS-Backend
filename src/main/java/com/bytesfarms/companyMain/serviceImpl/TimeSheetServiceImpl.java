package com.bytesfarms.companyMain.serviceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytesfarms.companyMain.entity.Break;
import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.TimeSheetRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.TimeSheetService;
import com.bytesfarms.companyMain.util.TimeSheetStatus;

import jakarta.persistence.EntityNotFoundException;

/*
 * @author Shivendra Singh
 */

@Service

public class TimeSheetServiceImpl implements TimeSheetService {

	@Autowired
	private TimeSheetRepository timeSheetRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public String checkIn(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		

		// Trim the time information to compare only the date
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startDateTime = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endDateTime = startDateTime.plusHours(23).plusMinutes(	59).plusSeconds(59);

		// Check if the user has already checked in on the current date
		if (timeSheetRepository.existsByUserIdAndCheckInTimeBetween(userId, startDateTime, endDateTime)) {
			return "User has already checked in on the current date.";
		}

		TimeSheet timeSheet = new TimeSheet();
		timeSheet.setUser(user);
		timeSheet.setCheckInTime(currentDateTime);
		timeSheet.setStatus(TimeSheetStatus.CHECKED_IN);
		timeSheet.setDay(currentDateTime.getDayOfWeek().toString());
		timeSheet.setMonth(currentDateTime.getMonth().toString());
		timeSheet.setYear(Integer.toString(currentDateTime.getYear()));
		timeSheetRepository.save(timeSheet);
		return "Checked In Successfully";
	}

	@Override
	@Transactional
	public String checkOut(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		Optional<TimeSheet> lastEntry = timeSheetRepository.findTopByUserIdOrderByIdDesc(userId);

		if (lastEntry.isEmpty() || lastEntry.get().getStatus().equals(TimeSheetStatus.CHECKED_OUT)) {
			return "User is not checked in.";
		}

		TimeSheet timeSheet = lastEntry.get();
		timeSheet.setCheckOutTime(LocalDateTime.now());
		timeSheet.setStatus(TimeSheetStatus.CHECKED_OUT);

		Duration totalWorkDuration = Duration.between(timeSheet.getCheckInTime(), timeSheet.getCheckOutTime());

		for (Break breakEntry : timeSheet.getBreaks()) {
			totalWorkDuration = totalWorkDuration
					.minus(Duration.between(breakEntry.getBreakStartTime(), breakEntry.getBreakEndTime()));
		}

		long hours = totalWorkDuration.toHours();
		long minutes = totalWorkDuration.toMinutesPart();
		long seconds = totalWorkDuration.toSecondsPart();

		timeSheet.setActualHours(hours);
		timeSheet.setActualMinutes(minutes);
		timeSheet.setActualSeconds(seconds);

		// Calculating if the person has been on half day , full day or He is absent

		long standardWorkingTime = 8 * 60 + 45; // This is current time in Bytesfarms to complete in day in Minutes

		boolean isHalfDay = totalWorkDuration.toMinutes() < standardWorkingTime
				|| totalWorkDuration.toMinutes() > (4 * 60 + 30);
		boolean isLeaveDay = totalWorkDuration.isZero() || totalWorkDuration.toMinutes() < (4 * 60 + 30);

		boolean isPresentDay = totalWorkDuration.toMinutes() == standardWorkingTime
				|| totalWorkDuration.toMinutes() > standardWorkingTime;

		timeSheet.setHalfDay(isHalfDay);
		timeSheet.setLeaveDay(isLeaveDay);
		timeSheet.setPresentDay(isPresentDay);
		timeSheetRepository.save(timeSheet);
		return "Checked Out Successfully";

	}

	@Override
	@Transactional
	public void startBreak(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		List<TimeSheet> todayTimeSheets = timeSheetRepository.findTodayTimeSheetByUserId(userId,
				LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX));

		if (todayTimeSheets.isEmpty() || todayTimeSheets.get(0).getStatus().equals(TimeSheetStatus.BREAK)) {
			throw new IllegalStateException("Invalid operation to start lunch break.");
		}

		TimeSheet timeSheet = todayTimeSheets.get(0);
		Break breakEntry = new Break();
		breakEntry.setBreakStartTime(LocalDateTime.now());
		breakEntry.setTimeSheet(timeSheet);
		timeSheet.setStatus(TimeSheetStatus.BREAK);
		timeSheet.getBreaks().add(breakEntry);
		timeSheetRepository.save(timeSheet);
	}

	@Override
	@Transactional
	public void endBreak(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		List<TimeSheet> todayTimeSheets = timeSheetRepository.findTodayTimeSheetByUserId(userId,
				LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX));

		if (todayTimeSheets.isEmpty() || !todayTimeSheets.get(0).getStatus().equals(TimeSheetStatus.BREAK)) {
			throw new IllegalStateException("Invalid operation to end lunch break.");
		}

		TimeSheet timeSheet = todayTimeSheets.get(0);
		Break breakEntry = timeSheet.getBreaks().get(timeSheet.getBreaks().size() - 1);
		breakEntry.setBreakEndTime(LocalDateTime.now());
		timeSheet.setStatus(TimeSheetStatus.CHECKED_IN);
		timeSheetRepository.save(timeSheet);
	}

	@Override
	public List<TimeSheet> calculateTotalHours(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		return timeSheetRepository.findByUserId(userId);
	}

}
