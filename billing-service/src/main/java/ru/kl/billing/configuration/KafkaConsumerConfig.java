package ru.kl.billing.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import ru.kl.billing.service.BillingService;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    private static final String USERS_TOPIC_NAME = "users";
    private static final String ORDERS_TOPIC_NAME = "orders";
    private static final String BILLING_GROUP_ID = "billGroup";
    private static final String KAFKA_CLIENT_HEADER = "client";

    private final BillingService billingService;
    private final String bootstrapAddress;

    public KafkaConsumerConfig(BillingService billingService, KafkaProperties properties) {
        this.billingService = billingService;
        this.bootstrapAddress = properties.getBootstrapAddress();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }

    private ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> properties = generateConsumerProperties(StringDeserializer.class, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(longConsumerFactory());
        return factory;
    }

    private ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> properties = generateConsumerProperties(StringDeserializer.class, LongDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    private Map<String, Object> generateConsumerProperties(
            Class<?> keyDeserializerClass,
            Class<?> valueDeserializerClass
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, BILLING_GROUP_ID);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        return properties;
    }

    @KafkaListener(
            topics = USERS_TOPIC_NAME,
            groupId = BILLING_GROUP_ID,
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void usersListener(@Payload String message){
        log.info("Get message: topic = {}, groupId = {}, message = {}", USERS_TOPIC_NAME, BILLING_GROUP_ID, message);
        billingService.createBill(message);
    }

    @KafkaListener(
            topics = ORDERS_TOPIC_NAME,
            groupId = BILLING_GROUP_ID,
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void ordersListener(ConsumerRecord<String, Long> record){
        log.info("Get message: {}", record);
        String clientId = new String(record.headers().lastHeader(KAFKA_CLIENT_HEADER).value());
        try {
            billingService.decrease(clientId, record.value(), record.key());
        } catch (IllegalArgumentException e) {
            log.error("Failure on bill decrease", e);
        }
    }
}
