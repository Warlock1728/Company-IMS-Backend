package com.bytesfarms.companyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.Resume;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

	List<Resume> findByJobPositionId(Long jobPositionId);
}