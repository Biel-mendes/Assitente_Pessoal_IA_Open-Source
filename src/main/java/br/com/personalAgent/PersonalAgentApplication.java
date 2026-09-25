package br.com.personalAgent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.SecureRandom;

@SpringBootApplication
@EnableScheduling
public class PersonalAgentApplication {

	public static void main(String[] args) {
        SpringApplication.run(PersonalAgentApplication.class, args);
	}

}
