package com.bluestar.springboot.basics.springbootin10steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.bluestar.springboot.basics.springbootin10steps.jpa.hibernate.CourseRepository;

@SpringBootApplication
public class SpringbootIn10StepsApplication implements CommandLineRunner {

	@Autowired
	private CourseRepository courseRepository;
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(SpringbootIn10StepsApplication.class, args);
		for (String name : applicationContext.getBeanDefinitionNames()) {
//			System.out.println(name);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		courseRepository.playWithEntityManager();

	}
}
