package ru.kl.order.service.implement;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kl.order.model.OrderDetails;
import ru.kl.order.model.OrderStatus;
import ru.kl.order.repository.OrderDetailsRepository;
import ru.kl.order.repository.OrderStatusRepository;
import ru.kl.order.service.OrderService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static ru.kl.order.configuration.KafkaProducerConfig.KAFKA_CLIENT_HEADER;
import static ru.kl.order.configuration.KafkaProducerConfig.ORDERS_TOPIC_NAME;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final KafkaTemplate<String, Long> producer;

    @Override
    public OrderDetails create(String clientId, Long amount) {
        Assert.hasText(clientId, "The given client must not be empty");
        Assert.isTrue(amount != null && amount > 0, "The given amount must not be empty or zero");
        OrderDetails orderDetails = orderDetailsRepository.insert(new OrderDetails(clientId, amount));
        sendCreateOrderEvent(orderDetails);
        return orderDetails;
    }

    private void sendCreateOrderEvent(OrderDetails order) {
        ProducerRecord<String, Long> producerRecord = new ProducerRecord<>(
                ORDERS_TOPIC_NAME,
                order.getId().toString(),
                order.getAmount()
        );
        producerRecord.headers().add(KAFKA_CLIENT_HEADER, order.getClientId().getBytes(StandardCharsets.UTF_8));
        producer.send(producerRecord);
    }

    @Override
    public OrderDetails getById(String clientId, String orderId) {
        Assert.hasText(clientId, "The given client must not be empty");
        Assert.hasText(orderId, "The given orderId must not be empty");
        return orderDetailsRepository.findById(UUID.fromString(orderId))
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public OrderStatus updateStatus(String orderId, String status, long insTime) {
        return orderStatusRepository.insert(
                new OrderStatus(
                        UUID.fromString(orderId),
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(insTime), ZoneId.systemDefault()),
                        status
                )
        );
    }
}
