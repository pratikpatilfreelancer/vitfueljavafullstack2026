package com.foodorder.payment;

public class UPIPayment implements Payment {
    private final String upiId;

    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean processPayment(double amount) {
        return upiId != null && upiId.contains("@");
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}
