package com.bytesfarms.companyMain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class Policy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Lob
	@Column(name = "content", columnDefinition = "LONGBLOB", length = Integer.MAX_VALUE)
	private String content;

//	@Lob
//	@Column(name = "pdf_data", columnDefinition = "LONGBLOB", length = Integer.MAX_VALUE)	
//	private byte[] pdfData;

	private LocalDateTime uploadDate;
}
