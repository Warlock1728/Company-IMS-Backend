package com.bytesfarms.companyMain.serviceImpl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytesfarms.companyMain.entity.TimeSheet;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.TimeSheetRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.TimeSheetService;
import com.bytesfarms.companyMain.util.TimeSheetStatus;

import jakarta.persistence.EntityNotFoundException;

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
		timeSheetRepository.save(timeSheet);
	}

	@Override
	@Transactional
	public void startBreak(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		Optional<TimeSheet> lastEntry = timeSheetRepository.findTopByUserIdOrderByIdDesc(userId);

		if (lastEntry.isEmpty() || lastEntry.get().getStatus().equals(TimeSheetStatus.BREAK)) {
			throw new IllegalStateException("Invalid operation to start lunch break.");
		}

		TimeSheet timeSheet = lastEntry.get();
		timeSheet.setBreakStartTime(LocalDateTime.now());
		timeSheet.setStatus(TimeSheetStatus.BREAK);
		timeSheetRepository.save(timeSheet);
	}

	@Override
	@Transactional
	public void endBreak(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		Optional<TimeSheet> lastEntry = timeSheetRepository.findTopByUserIdOrderByIdDesc(userId);

		if (lastEntry.isEmpty() || !lastEntry.get().getStatus().equals(TimeSheetStatus.BREAK)) {
			throw new IllegalStateException("Invalid operation to end lunch break.");
		}

		TimeSheet timeSheet = lastEntry.get();
		timeSheet.setBreakEndTime(LocalDateTime.now());
		timeSheet.setStatus(TimeSheetStatus.CHECKED_IN);
		timeSheetRepository.save(timeSheet);
	}

	@Override
	public Duration calculateTotalHours(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		BigDecimal totalWorkSeconds = timeSheetRepository.calculateTotalWorkDuration(userId);
		return Duration.ofSeconds(totalWorkSeconds.longValue());
	}
}
