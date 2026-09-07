package com.matrimonial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatrimonialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatrimonialApplication.class, args);
        System.out.println("Matrimonial JPA Backend running at http://localhost:8080");
    }
}
