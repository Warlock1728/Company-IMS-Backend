package com.bytesfarms.companyMain.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class PdfUtil {

	public static ResponseEntity<byte[]> createResponse(byte[] content, String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", filename);
		return ResponseEntity.ok().headers(headers).body(content);
	}

}
