package com.microservices.event.orderws;

import com.microservices.event.core.configuration.AxonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@EnableDiscoveryClient
@SpringBootApplication
@Import({AxonConfig.class})
public class OrderWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderWsApplication.class, args);
    }

}
