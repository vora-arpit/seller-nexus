package com.server.sellernexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.server.sellernexus.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SellerNexusApplication {

	public static void main(String[] args) {
		
//		String encode = new BCryptPasswordEncoder().encode("saq123");
//		System.out.println(encode);
		
		SpringApplication.run(SellerNexusApplication.class, args);
		
	}
}
