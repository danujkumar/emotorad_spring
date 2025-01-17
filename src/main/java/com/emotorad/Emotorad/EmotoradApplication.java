package com.emotorad.Emotorad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EmotoradApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmotoradApplication.class, args);
	}
}
