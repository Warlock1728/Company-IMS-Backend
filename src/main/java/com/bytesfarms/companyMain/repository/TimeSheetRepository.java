package com.bytesfarms.companyMain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytesfarms.companyMain.entity.TimeSheet;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {

	Optional<TimeSheet> findTopByUserIdOrderByIdDesc(Long userId);

	@Query("SELECT t FROM TimeSheet t " + "WHERE t.user.id = :userId " + "AND t.checkInTime >= :startOfDay "
			+ "AND t.checkInTime < :endOfDay " + "ORDER BY t.id DESC")
	List<TimeSheet> findTodayTimeSheetByUserId(@Param("userId") Long userId,
			@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

}
