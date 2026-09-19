package com.foodorder.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot backend for the Online Food Ordering System.
 *
 * Start this class to run the REST API on http://localhost:8080.
 * The existing Java Swing desktop UI remains available through MainApp.
 */
@SpringBootApplication
public class FoodOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderApplication.class, args);
    }
}
