package com.bytesfarms.companyMain.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bytesfarms.companyMain.entity.Resume;

public interface ResumeService {

	Long saveResume(String fileName, MultipartFile file, Long jobPositionId);

	List<Resume> getResumesByJobPositionId(Long jobPositionId);

}