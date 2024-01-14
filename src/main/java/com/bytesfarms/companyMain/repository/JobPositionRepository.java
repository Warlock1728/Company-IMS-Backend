package com.bytesfarms.companyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytesfarms.companyMain.entity.Resume;
import com.bytesfarms.companyMain.entity.JobPosition;

import jakarta.transaction.Transactional;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

	@Transactional
	@Modifying
	@Query("UPDATE JobPosition j SET j.applications = :applications WHERE j.id = :jobPositionId")
	void updateApplications(@Param("jobPositionId") Long jobPositionId,
			@Param("applications") List<Resume> applications);

}
