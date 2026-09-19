package com.foodorder.model;

public class OrderItem {
    private int foodId;
    private String foodName;
    private int quantity;
    private double price;

    public OrderItem() {}

    public OrderItem(int foodId, String foodName, int quantity, double price) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public double getLineTotal() {
        return price * quantity;
    }
}
