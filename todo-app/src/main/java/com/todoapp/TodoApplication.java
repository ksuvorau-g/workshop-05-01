package com.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class.
 */
@SpringBootApplication(scanBasePackages = "com.todoapp")
@EnableJpaRepositories(basePackages = "com.todoapp.core.repository")
public class TodoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}
