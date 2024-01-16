package com.bytesfarms.companyMain.service;

import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.dto.JobPositionDTO;
import com.bytesfarms.companyMain.entity.Resume;
import com.bytesfarms.companyMain.entity.JobPosition;

public interface JobPositionService {

	// crud operations on positions for jobs

	List<JobPosition> getAllJobPositions();

	Long createJobPosition(JobPositionDTO jobPositionDTO);

	void updateJobPosition(Long id, JobPositionDTO jobPositionDTO);

	void deleteJobPosition(Long id);

	// applicants , add , review and shortlisting


	JobPositionDTO reviewApplications(Long jobPositionId);

	JobPositionDTO shortlistCandidates(Long jobPositionId, List<Long> applicationIds);



}
