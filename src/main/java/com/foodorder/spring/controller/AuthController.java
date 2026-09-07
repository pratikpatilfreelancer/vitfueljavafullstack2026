package com.foodorder.spring.controller;

import com.foodorder.db.CustomerDAO;
import com.foodorder.exception.InvalidLoginException;
import com.foodorder.model.Customer;
import com.foodorder.spring.dto.LoginRequest;
import com.foodorder.spring.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Customer customer = new Customer(
                    0,
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getPhone(),
                    request.getAddress()
            );
            customerDAO.register(customer);
            return ResponseEntity.ok(customer);
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(customerDAO.login(request.getEmail(), request.getPassword()));
        } catch (SQLException | InvalidLoginException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(customerDAO.adminLogin(request.getEmail(), request.getPassword()));
        } catch (SQLException | InvalidLoginException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
