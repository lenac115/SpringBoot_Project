package com.springProject;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableAspectJAutoProxy
public class SpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApplication.class, args);
	}

}
