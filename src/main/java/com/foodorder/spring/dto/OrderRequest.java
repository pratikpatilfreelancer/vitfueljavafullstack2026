package com.foodorder.spring.dto;

import com.foodorder.model.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    private int customerId;
    private String paymentMethod;
    private List<OrderItem> orderItems = new ArrayList<>();

    public OrderRequest() {}

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
