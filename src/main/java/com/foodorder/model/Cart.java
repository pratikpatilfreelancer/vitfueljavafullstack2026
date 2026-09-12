package com.foodorder.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();
    private double discountRate = 0.0;   // e.g. 0.10 = 10%
    private static final double TAX_RATE = 0.05;   // 5% tax
    private static final double DELIVERY_FEE = 40.0;

    public void addItem(FoodItem food, int quantity) {
        for (CartItem item : items) {
            if (item.getFoodItem().getId() == food.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(food, quantity));
    }

    public void removeItem(int foodId) {
        items.removeIf(item -> item.getFoodItem().getId() == foodId);
    }

    public void updateQuantity(int foodId, int quantity) {
        for (CartItem item : items) {
            if (item.getFoodItem().getId() == foodId) {
                if (quantity <= 0) {
                    removeItem(foodId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        double sum = 0;
        for (CartItem item : items) sum += item.getSubtotal();
        return sum;
    }

    /** Automatically applies a 10% discount once the subtotal passes 500. */
    public double getDiscount() {
        double subtotal = getSubtotal();
        discountRate = subtotal >= 500 ? 0.10 : 0.0;
        return subtotal * discountRate;
    }

    public double getTax() {
        return (getSubtotal() - getDiscount()) * TAX_RATE;
    }

    public double getDeliveryFee() {
        return items.isEmpty() ? 0.0 : DELIVERY_FEE;
    }

    public double getTotal() {
        return getSubtotal() - getDiscount() + getTax() + getDeliveryFee();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
