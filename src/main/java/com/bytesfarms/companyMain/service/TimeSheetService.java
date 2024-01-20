package com.bytesfarms.companyMain.service;

import java.time.Duration;
import java.util.List;

import com.bytesfarms.companyMain.entity.TimeSheet;

public interface TimeSheetService {

	String checkIn(Long userId);

	String checkOut(Long userId);

	void startBreak(Long userId);

	void endBreak(Long userId);

	List<TimeSheet> calculateTotalHours(Long userId);

}
