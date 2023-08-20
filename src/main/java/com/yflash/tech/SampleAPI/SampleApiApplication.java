package com.yflash.tech.SampleAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SampleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleApiApplication.class, args);
	}

}
