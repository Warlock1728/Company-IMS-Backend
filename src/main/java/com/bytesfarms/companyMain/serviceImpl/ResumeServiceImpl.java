package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.JobPosition;
import com.bytesfarms.companyMain.entity.Resume;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.JobPositionRepository;
import com.bytesfarms.companyMain.repository.ResumeRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.ResumeService;
import com.bytesfarms.companyMain.util.ApplicationStatus;

@Service
public class ResumeServiceImpl implements ResumeService {

	@Autowired
	private ResumeRepository resumeRepository;

	@Autowired
	private JobPositionRepository jobPositionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public Long saveResume(String fileName, MultipartFile file, Long jobPositionId, Long userId) {
		try {

			Optional<JobPosition> optionalJobPosition = jobPositionRepository.findById(jobPositionId);

			Optional<User> optionalUser = userRepository.findById(userId);

			if (optionalJobPosition.isPresent()) {
				Resume resume = new Resume();
				resume.setFileName(fileName);
				resume.setFileData(file.getBytes());

				resume.setJobPosition(optionalJobPosition.get());
				resume.setUser(optionalUser.get());
				resume.setStatus(ApplicationStatus.SUBMITTED);
				sendSubmissionNotificationEmail(optionalUser.get(), optionalJobPosition.get());
				return resumeRepository.save(resume).getId();
			} else {

				return null;
			}
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Resume> getResumesByJobPositionId(Long jobPositionId) {
		return resumeRepository.findByJobPositionId(jobPositionId);

	}

	private void sendSubmissionNotificationEmail(User user, JobPosition jobPosition) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Resume Submission Notification");

		String jobTitle = jobPosition.getTitle();
		message.setText("Thank you for submitting your resume for the position: " + jobTitle
				+ ". We have received your application. "
				+ "Once reviewed you will be updated with status of the same.");

		javaMailSender.send(message);
	}

	@Override
	public boolean updateResumeStatus(Long resumeId, String status, Long jobPositionId) {
		Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
		Optional<JobPosition> optionalJobPosition = jobPositionRepository.findById(jobPositionId);

		if (optionalResume.isPresent()) {
			Resume resume = optionalResume.get();
			resume.setStatus(ApplicationStatus.valueOf(status.toUpperCase()));

			resumeRepository.save(resume);

			if (status.equalsIgnoreCase(ApplicationStatus.SHORTLISTED.name())) {
				sendShortlistNotificationEmail(resume.getUser(), optionalJobPosition.get());
			}

			return true;
		}

		return false;
	}

	private void sendShortlistNotificationEmail(User user, JobPosition jobPosition) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Application Shortlisted Notification");
		String jobTitle = jobPosition.getTitle();
		message.setText("Congratulations! Your job application has been shortlisted for the position: " + jobTitle
				+ ". We will let you know once the interview is scheduled.");

		javaMailSender.send(message);
	}

}