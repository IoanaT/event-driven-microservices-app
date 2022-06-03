package com.microservices.event.paymentsws;

import com.microservices.event.core.configuration.AxonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonConfig.class})
@EnableDiscoveryClient
public class PaymentsWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsWsApplication.class, args);
	}

}
