package com.microservices.event.productws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ProductWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductWsApplication.class, args);
	}

}
