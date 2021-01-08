package ru.kl.order.service;

import ru.kl.order.model.OrderDetails;
import ru.kl.order.model.OrderStatus;

public interface OrderService {

    OrderDetails create(String clientId, Long amount);
    OrderDetails getById(String clientId, String orderId);
    OrderStatus updateStatus(String orderId, String status, long insTime);
}
