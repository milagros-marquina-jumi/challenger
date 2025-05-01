package com.challenge.challenger;

import org.springframework.boot.SpringApplication;

public class TestChallengerApplication {

	public static void main(String[] args) {
		SpringApplication.from(ChallengerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
