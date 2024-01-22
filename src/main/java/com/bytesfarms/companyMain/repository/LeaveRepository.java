package com.bytesfarms.companyMain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytesfarms.companyMain.entity.LeaveRequest;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

	List<LeaveRequest> findByUserId(Long userId);

	 Optional<LeaveRequest> findTopByUserIdAndQuarterOrderByStartDateDesc(Long userId, String quarter);

	List<LeaveRequest> findByUserIdAndQuarter(Long userId, String quarter);
	

}