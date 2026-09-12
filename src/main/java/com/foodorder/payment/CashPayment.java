package com.foodorder.payment;

public class CashPayment implements Payment {
    @Override
    public boolean processPayment(double amount) {
        // Cash on delivery is always accepted at order time.
        return true;
    }

    @Override
    public String getMethodName() {
        return "Cash on Delivery";
    }
}
