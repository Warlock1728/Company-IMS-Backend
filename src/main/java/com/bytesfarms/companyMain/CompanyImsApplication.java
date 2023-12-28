package com.bytesfarms.companyMain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.bytesfarms.companyMain.entity")

@SpringBootApplication(scanBasePackages = "com.bytesfarms")

public class CompanyImsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyImsApplication.class, args);
	}

}
