package com.bytesfarms.companyMain.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import com.bytesfarms.companyMain.util.IMSConstants;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

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

	@Autowired
	EmailSender emailSender;

	@Override
	public Long saveResume(MultipartFile file, Long jobPositionId, Long userId, String lastJobTitle,
			Integer lastJobExperience, String lastJobCompany, BigDecimal expectedSalary)
			throws AddressException, MessagingException {
		try {

			Optional<JobPosition> optionalJobPosition = jobPositionRepository.findById(jobPositionId);

			Optional<User> optionalUser = userRepository.findById(userId);

			if (optionalJobPosition.isPresent()) {
				Resume resume = new Resume();
				// resume.setFileName(fileName);
				resume.setFileData(file.getBytes());

				resume.setJobPosition(optionalJobPosition.get());
				resume.setUser(optionalUser.get());
				resume.setStatus(ApplicationStatus.SUBMITTED);

				resume.setLastJobTitle(lastJobTitle);
				resume.setLastJobExperience(lastJobExperience);
				resume.setLastJobCompany(lastJobCompany);
				resume.setExpectedSalary(expectedSalary);

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

	private void sendSubmissionNotificationEmail(User user, JobPosition jobPosition)
			throws AddressException, MessagingException {
		HashMap<String, String> map = new HashMap<>();

		map.put("jobPost", jobPosition.getTitle());
		map.put("userName", user.getUsername());

		String subject = "Application Receieved !";
		String emailTemplate = loadHtmlTemplate("/ResumeSubmitted.html");

		emailSender.sendEmail(IMSConstants.RECEIPIENT, emailTemplate, subject, map);
	}

	private String loadHtmlTemplate(String templatePath) {
		try (InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
				Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			return scanner.useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new RuntimeException("Error loading HTML template", e);
		}
	}

	@Override
	public boolean updateResumeStatus(Long resumeId, String status, Long jobPositionId) throws AddressException, MessagingException {
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

	private void sendShortlistNotificationEmail(User user, JobPosition jobPosition) throws AddressException, MessagingException {
		HashMap<String, String> map = new HashMap<>();

		map.put("jobPost", jobPosition.getTitle());
		map.put("userName", user.getUsername());

		String subject = "You are Shortlisted !";
		String emailTemplate = loadHtmlTemplate("/ResumeShortlisted.html");

		emailSender.sendEmail(IMSConstants.RECEIPIENT, emailTemplate, subject, map);
	}

}