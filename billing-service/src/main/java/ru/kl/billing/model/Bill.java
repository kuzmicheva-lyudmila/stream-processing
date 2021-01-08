package ru.kl.billing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "bill")
public class Bill {

    @Id
    private String clientId;

    private long balance;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updateTime;

    public Bill(String clientId) {
        this.clientId = clientId;
        this.balance = 0;
        this.updateTime = LocalDateTime.now();
    }
}
