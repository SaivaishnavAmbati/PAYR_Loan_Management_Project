package com.api_gateway.payr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PayrApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayrApplication.class, args);
	}

}
