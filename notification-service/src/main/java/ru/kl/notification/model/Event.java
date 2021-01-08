package ru.kl.notification.model;

import lombok.Data;

@Data
public class Event {

    private String orderId;

    private String status;

    public Event(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
