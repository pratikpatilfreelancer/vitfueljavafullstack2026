package com.foodorder.payment;

public class CardPayment implements Payment {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean processPayment(double amount) {
        // Simple validation to simulate a real gateway check.
        String digitsOnly = cardNumber.replaceAll("\\s+", "");
        return digitsOnly.length() >= 12 && digitsOnly.matches("\\d+");
    }

    @Override
    public String getMethodName() {
        return "Card";
    }
}
