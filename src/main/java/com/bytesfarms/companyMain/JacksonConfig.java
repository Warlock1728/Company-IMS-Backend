package com.bytesfarms.companyMain;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizeJackson() {
		return builder -> {
			builder.modules(new JavaTimeModule());
			
		};
	}

	@Bean
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.build();
	}
	
	
}
