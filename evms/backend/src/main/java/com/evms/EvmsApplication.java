package com.evms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Expense Voucher Management System (EVMS).
 * <p>
 * This class bootstraps the Spring Boot application, which auto-configures:
 * <ul>
 *   <li>Spring MVC (REST controllers)</li>
 *   <li>Spring Data JPA (MySQL via Hibernate)</li>
 *   <li>Spring Security (JWT-based authentication)</li>
 *   <li>Embedded Tomcat on port 4000</li>
 * </ul>
 *
 * @author EVMS Team
 * @version 1.0.0
 */
@SpringBootApplication
public class EvmsApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (none required)
     */
    public static void main(String[] args) {
        SpringApplication.run(EvmsApplication.class, args);
    }
}
