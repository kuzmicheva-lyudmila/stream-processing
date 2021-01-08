package ru.kl.order.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("order_details")
@Data
public class OrderDetails {

    @PrimaryKey
    private UUID id;

    @Column("client_id")
    private String clientId;

    private long amount;

    @Column("ins_time")
    private LocalDateTime insTime;

    public OrderDetails(String clientId, long amount) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.amount = amount;
        this.insTime = LocalDateTime.now();
    }
}
