package com.lalitVerma.cloudShare;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CloudShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudShareApplication.class, args);
	}

	@Autowired
	Environment env;

	@PostConstruct
	public void checkMongoProps() {
		System.out.println("URI = " + env.getProperty("spring.data.mongodb.uri"));
		System.out.println("DB  = " + env.getProperty("spring.data.mongodb.database"));
	}

}
