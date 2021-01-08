package ru.kl.billing.service;

public interface BillingService {

    void createBill(String clientId);
    void increase(String clientId, Long amount);
    void decrease(String clientId, Long amount, String orderId);
    long getBalance(String clientId);
}
