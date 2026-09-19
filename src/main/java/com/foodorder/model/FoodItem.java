package com.foodorder.model;

public class FoodItem {
    private int id;
    private String name;
    private Category category;
    private double price;
    private int quantity;      // stock available
    private boolean availability;

    public FoodItem() {}

    public FoodItem(int id, String name, Category category, double price, int quantity, boolean availability) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.availability = availability;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    @Override
    public String toString() {
        return name;
    }
}
