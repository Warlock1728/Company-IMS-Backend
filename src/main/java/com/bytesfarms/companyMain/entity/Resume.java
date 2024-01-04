package com.bytesfarms.companyMain.entity;

import com.bytesfarms.companyMain.util.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "resumes")
@Data
public class Resume {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "file_name")
	private String fileName;

	@Lob
	@Column(name = "file_data", columnDefinition = "LONGBLOB", length = Integer.MAX_VALUE)
	private byte[] fileData;

	@ManyToOne
	@JoinColumn(name = "job_position_id", nullable = false)
	private JobPosition jobPosition;
	

	@ManyToOne
	@JoinColumn(name = "user_id", nullable=false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ApplicationStatus status;

}
