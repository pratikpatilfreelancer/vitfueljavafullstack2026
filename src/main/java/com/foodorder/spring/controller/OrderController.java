package com.foodorder.spring.controller;

import com.foodorder.db.OrderDAO;
import com.foodorder.model.Order;
import com.foodorder.spring.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderDAO orderDAO = new OrderDAO();

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Order must contain at least one item."));
        }

        try {
            double total = request.getOrderItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            Order order = new Order(
                    request.getCustomerId(),
                    total,
                    "PLACED",
                    request.getPaymentMethod()
            );
            request.getOrderItems().forEach(order::addOrderItem);

            int orderId = orderDAO.placeOrder(order);
            order.setOrderId(orderId);
            return ResponseEntity.ok(order);
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to place order: " + e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerOrders(@PathVariable int customerId) {
        try {
            return ResponseEntity.ok(orderDAO.getOrdersByCustomer(customerId));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Unable to load orders: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderDAO.getAllOrders());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Unable to load orders: " + e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int orderId,
                                           @RequestParam String status) {
        try {
            orderDAO.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(Map.of(
                    "orderId", orderId,
                    "status", status
            ));
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to update order: " + e.getMessage()));
        }
    }
}
