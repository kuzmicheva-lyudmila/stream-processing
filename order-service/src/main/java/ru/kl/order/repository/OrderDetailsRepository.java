package ru.kl.order.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import ru.kl.order.model.OrderDetails;

import java.util.UUID;

public interface OrderDetailsRepository extends CassandraRepository<OrderDetails, UUID> {
}
