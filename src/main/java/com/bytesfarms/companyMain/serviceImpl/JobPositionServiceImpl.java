package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.dto.JobPositionDTO;

import com.bytesfarms.companyMain.entity.JobPosition;
import com.bytesfarms.companyMain.repository.JobPositionRepository;
import com.bytesfarms.companyMain.service.JobPositionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobPositionServiceImpl implements JobPositionService {

	@Autowired
	private JobPositionRepository jobPositionRepository;

	@Override
	public List<JobPosition> getAllJobPositions() {
		return jobPositionRepository.findAll();
	}

	@Override
	public Long createJobPosition(JobPositionDTO jobPositionDTO) {
		JobPosition jobPosition = new JobPosition();
		jobPosition.setTitle(jobPositionDTO.getTitle());
		jobPosition.setOpenings(jobPositionDTO.getOpenings());
		jobPosition.setExperience(jobPositionDTO.getExperience());
		jobPosition.setRequirements(jobPositionDTO.getRequirements());

		JobPosition savedJobPosition = jobPositionRepository.save(jobPosition);
		return savedJobPosition.getId();
	}

	@Override
	public void updateJobPosition(Long id, JobPositionDTO jobPositionDTO) {
		JobPosition existingJobPosition = jobPositionRepository.findById(id).orElse(null);

		if (existingJobPosition == null) {
			throw new EntityNotFoundException("Job position with ID " + id + " not found");
		}

		existingJobPosition.setTitle(jobPositionDTO.getTitle());
		existingJobPosition.setOpenings(jobPositionDTO.getOpenings());
		existingJobPosition.setExperience(jobPositionDTO.getExperience());
		existingJobPosition.setRequirements(jobPositionDTO.getRequirements());

		jobPositionRepository.save(existingJobPosition);
	}

	@Override
	public void deleteJobPosition(Long id) {
		if (!jobPositionRepository.existsById(id)) {
			throw new EntityNotFoundException("Job position with ID " + id + " not found");
		}

		jobPositionRepository.deleteById(id);
	}

	// For applicans to add and admins to review and shortlist candidates.

	@Override
	public JobPositionDTO reviewApplications(Long jobPositionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobPositionDTO shortlistCandidates(Long jobPositionId, List<Long> applicationIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobPositionDTO scheduleInterviews(Long jobPositionId, List<Long> applicationIds, String interviewDateTime) {
		// TODO Auto-generated method stub
		return null;
	}

}