package com.fitiz.csredisconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.fitiz.csredisconsumer"})
public class CsRedisConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsRedisConsumerApplication.class, args);
	}

}
