package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.JobPosition;
import com.bytesfarms.companyMain.entity.Resume;
import com.bytesfarms.companyMain.repository.JobPositionRepository;
import com.bytesfarms.companyMain.repository.ResumeRepository;
import com.bytesfarms.companyMain.service.ResumeService;

@Service
public class ResumeServiceImpl implements ResumeService {

	@Autowired
	private ResumeRepository resumeRepository;

	@Autowired
	private JobPositionRepository jobPositionRepository;

	@Override
	public Long saveResume(String fileName, MultipartFile file, Long jobPositionId) {
		try {

			Optional<JobPosition> optionalJobPosition = jobPositionRepository.findById(jobPositionId);

			if (optionalJobPosition.isPresent()) {
				Resume resume = new Resume();
				resume.setFileName(fileName);
				resume.setFileData(file.getBytes());

				resume.setJobPosition(optionalJobPosition.get());

				return resumeRepository.save(resume).getId();
			} else {

				return null;
			}
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
}