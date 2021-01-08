package ru.kl.order.model;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("order_status")
@Data
public class OrderStatus {

    @PrimaryKeyColumn(
            value = "id",
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 0
    )
    private UUID id;

    @PrimaryKeyColumn(
            value = "ins_time",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING,
            ordinal = 1
    )
    private LocalDateTime insTime;

    private String status;

    public OrderStatus(UUID id, LocalDateTime insTime, String status) {
        this.id = id;
        this.insTime = insTime;
        this.status = status;
    }
}
