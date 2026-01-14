package com.messageapp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
@EnableFeignClients
public class MessageAppApplication {

	@PostConstruct
	public void setDefaultTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("=== Application timezone set to: {} ===", TimeZone.getDefault().getID());
		log.info("=== Current server time: {} ===", LocalDateTime.now());
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageAppApplication.class, args);
	}

}
