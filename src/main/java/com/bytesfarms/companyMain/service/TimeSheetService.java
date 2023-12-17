package com.bytesfarms.companyMain.service;

import java.time.Duration;

public interface TimeSheetService {

	void checkIn(Long userId);

	void checkOut(Long userId);

	void startBreak(Long userId);

	void endBreak(Long userId);

	Duration calculateTotalHours(Long userId);

}
