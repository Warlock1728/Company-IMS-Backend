package com.bytesfarms.companyMain.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.dto.JobApplicationDTO;
import com.bytesfarms.companyMain.dto.JobPositionDTO;
import com.bytesfarms.companyMain.entity.JobApplication;
import com.bytesfarms.companyMain.entity.JobPosition;
import com.bytesfarms.companyMain.repository.JobPositionRepository;
import com.bytesfarms.companyMain.service.JobPositionService;

import jakarta.persistence.EntityNotFoundException;

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
	public String addApplication(Long jobPositionId, JobApplicationDTO applicationDTO) {
		JobPosition jobPosition = jobPositionRepository.findById(jobPositionId)
				.orElseThrow(() -> new EntityNotFoundException("Job position with ID " + jobPositionId + " not found"));

		JobApplication jobApplication = new JobApplication();
		jobApplication.setResume(applicationDTO.getResume());
		jobApplication.setCoverLetter(applicationDTO.getCoverLetter());
		jobApplication.setApplicationDate(LocalDateTime.now());
		jobApplication.setShortlisted(false);
		jobApplication.setInterviewScheduled(false);

		jobApplication.setJobPosition(jobPosition);
		jobPosition.getApplications().add(jobApplication);

		jobPositionRepository.save(jobPosition);

		return "Successfully Applied to Job";
	}

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