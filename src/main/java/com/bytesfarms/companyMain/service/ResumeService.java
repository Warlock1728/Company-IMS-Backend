package com.bytesfarms.companyMain.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.Resume;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface ResumeService {

	

	List<Resume> getResumesByJobPositionId(Long jobPositionId);

	boolean updateResumeStatus(Long resumeId, String status, Long jobPositionId) throws AddressException, MessagingException;

	Long saveResume(MultipartFile file, Long jobPositionId, Long userId, String lastJobTitle, Integer lastJobExperience,
			String lastJobCompany, BigDecimal expectedSalary) throws AddressException, MessagingException;

}