package ru.kl.billing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "bill_decrease")
public class BillDecrease {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String clientId;

    private Long amount;

    private String orderId;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime insTime;

    public BillDecrease(String clientId, Long amount, String orderId) {
        this.clientId = clientId;
        this.amount = amount;
        this.orderId = orderId;
        this.insTime = LocalDateTime.now();
    }
}
