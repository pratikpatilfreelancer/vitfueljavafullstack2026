package com.foodorder.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private Timestamp orderDate;
    private double totalAmount;
    private String status;   // PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    private String paymentMethod;
    private final List<OrderItem> orderItems = new ArrayList<>();

    public Order() { }

    public Order(int customerId, double totalAmount, String status, String paymentMethod) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void addOrderItem(OrderItem item) { orderItems.add(item); }
}
