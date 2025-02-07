package com.tcube.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@Configuration
//@ComponentScan("com.tcube.api.*")
public class TcubeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcubeApplication.class, args);
	}

}
