package ru.kl.notification.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import ru.kl.notification.model.Event;
import ru.kl.notification.service.NotificationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    private static final String BILLS_TOPIC_NAME = "bills";
    private static final String KAFKA_CLIENT_HEADER = "client";

    private final String bootstrapAddress;
    private final NotificationService notificationService;

    public KafkaConsumerConfig(
            @Value(value = "${kafka.bootstrap-address}") String bootstrapAddress,
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
        this.bootstrapAddress = bootstrapAddress;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @KafkaListener(topics=BILLS_TOPIC_NAME)
    public void billListener(ConsumerRecord<String, String> record){
        log.info("Get message: record = {}", record);
        String clientId = new String(record.headers().lastHeader(KAFKA_CLIENT_HEADER).value());
        notificationService.create(clientId, new Event(record.key(), record.value()));
    }
}
