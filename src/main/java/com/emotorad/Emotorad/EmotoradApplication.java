package com.emotorad.Emotorad;

import org.springframework.boot.SpringApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EmotoradApplication {
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("MONGO_ID", dotenv.get("MONGO_ID"));
		System.setProperty("MONGO_PASSWORD", dotenv.get("MONGO_PASSWORD"));

		SpringApplication.run(EmotoradApplication.class, args);
	}
}
