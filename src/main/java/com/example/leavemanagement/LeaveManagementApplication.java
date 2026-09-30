package com.example.leavemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Employee Leave Management System.
 *
 * @SpringBootApplication is a shortcut annotation that combines:
 *   - @Configuration   : marks this class as a source of bean definitions
 *   - @EnableAutoConfiguration : lets Spring Boot auto-configure the app
 *                                (embedded Tomcat, Thymeleaf, JPA, etc.)
 *   - @ComponentScan   : tells Spring to scan this package (and sub-packages)
 *                        for @Controller, @Service, @Repository classes
 *
 * Running this class starts an embedded web server on http://localhost:8080
 */
@SpringBootApplication
public class LeaveManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementApplication.class, args);
    }
}
