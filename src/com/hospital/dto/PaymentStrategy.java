package com.hospital.dto;

public interface PaymentStrategy {
    boolean processPayment(double amount);
}
