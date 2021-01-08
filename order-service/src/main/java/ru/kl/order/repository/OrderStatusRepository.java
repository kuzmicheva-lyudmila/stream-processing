package ru.kl.order.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import ru.kl.order.model.OrderStatus;

import java.util.UUID;

public interface OrderStatusRepository extends CassandraRepository<OrderStatus, UUID> {
}
