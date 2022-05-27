package com.microservices.event.orderws.orderws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OrderWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderWsApplication.class, args);
	}

}
