package com.bytesfarms.companyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.LeaveRequest;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
	
	
}