package com.server.crm1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.server.crm1.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class CRM1Application {

	public static void main(String[] args) {
		
//		String encode = new BCryptPasswordEncoder().encode("saq123");
//		System.out.println(encode);
		
		SpringApplication.run(CRM1Application.class, args);
		
	}
}
