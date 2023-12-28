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
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	public void checkIn(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		Optional<TimeSheet> lastEntry = timeSheetRepository.findTopByUserIdOrderByIdDesc(userId);

		if (lastEntry.isPresent() && lastEntry.get().getStatus().equals(TimeSheetStatus.CHECKED_IN)) {
			throw new IllegalStateException("User is already checked in.");
		}

		TimeSheet timeSheet = new TimeSheet();
		timeSheet.setUser(user);
		timeSheet.setCheckInTime(LocalDateTime.now());
		timeSheet.setStatus(TimeSheetStatus.CHECKED_IN);
		timeSheet.setDay(LocalDate.now().getDayOfWeek().toString());
		timeSheet.setMonth(LocalDate.now().getMonth().toString());
		timeSheet.setYear(Integer.toString(LocalDate.now().getYear()));
		timeSheetRepository.save(timeSheet);
	}

	@Override
	@Transactional
	public void checkOut(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		Optional<TimeSheet> lastEntry = timeSheetRepository.findTopByUserIdOrderByIdDesc(userId);

		if (lastEntry.isEmpty() || lastEntry.get().getStatus().equals(TimeSheetStatus.CHECKED_OUT)) {
			throw new IllegalStateException("User is not checked in.");
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

		timeSheetRepository.save(timeSheet);
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
