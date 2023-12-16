package com.bytesfarms.companyMain.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

	Long saveResume(String fileName, MultipartFile file, Long jobPositionId);

}