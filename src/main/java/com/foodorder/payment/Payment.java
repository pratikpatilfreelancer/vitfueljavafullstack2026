package com.foodorder.payment;

public interface Payment {
    /** Returns true if the payment succeeded. */
    boolean processPayment(double amount);

    String getMethodName();
}
