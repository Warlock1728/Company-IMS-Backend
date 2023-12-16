package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.dto.JobApplicationDTO;
import com.bytesfarms.companyMain.dto.JobPositionDTO;
import com.bytesfarms.companyMain.entity.JobPosition;

public interface JobPositionService {

	// crud operations on positions for jobs

	List<JobPosition> getAllJobPositions();

	Long createJobPosition(JobPositionDTO jobPositionDTO);

	void updateJobPosition(Long id, JobPositionDTO jobPositionDTO);

	void deleteJobPosition(Long id);

	// applicants , add , review and shortlisting

	String addApplication(Long jobPositionId, JobApplicationDTO applicationDTO);

	JobPositionDTO reviewApplications(Long jobPositionId);

	JobPositionDTO shortlistCandidates(Long jobPositionId, List<Long> applicationIds);

	JobPositionDTO scheduleInterviews(Long jobPositionId, List<Long> applicationIds, String interviewDateTime);

}
