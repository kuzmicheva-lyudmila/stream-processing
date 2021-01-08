package ru.kl.billing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "bill_increase")
public class BillIncrease {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String clientId;

    private Long amount;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime insTime;

    public BillIncrease(String clientId, Long amount) {
        this.clientId = clientId;
        this.amount = amount;
        this.insTime = LocalDateTime.now();
    }
}
