package com.microservices.event.usersws;

import com.microservices.event.core.configuration.AxonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonConfig.class})
public class UsersWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersWsApplication.class, args);
	}

}
