package ru.kl.billing.service.implement;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.kl.billing.model.Bill;
import ru.kl.billing.model.BillDecrease;
import ru.kl.billing.model.BillIncrease;
import ru.kl.billing.repository.BillDecreaseRepository;
import ru.kl.billing.repository.BillIncreaseRepository;
import ru.kl.billing.repository.BillRepository;
import ru.kl.billing.service.BillingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static ru.kl.billing.configuration.KafkaProducerConfig.BILLS_TOPIC_NAME;
import static ru.kl.billing.configuration.KafkaProducerConfig.KAFKA_CLIENT_HEADER;

@Service
@RequiredArgsConstructor
public class DefaultBillingService implements BillingService {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAILURE_STATUS = "failure";

    private final BillRepository billRepository;
    private final BillDecreaseRepository billDecreaseRepository;
    private final BillIncreaseRepository billIncreaseRepository;
    private final KafkaTemplate<String, String> producer;

    @Override
    @Transactional
    public void createBill(String clientId) {
        checkClientId(clientId);
        billRepository.save(new Bill(clientId));
    }

    @Override
    @Transactional
    public void increase(String clientId, Long amount) {
        checkRequestData(clientId, amount);
        billRepository.getBillBalanceWithLock(clientId);
        billIncreaseRepository.save(new BillIncrease(clientId, amount));
        billRepository.setBillBalance(amount, clientId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void decrease(String clientId, Long amount, String orderId) {
        checkRequestData(clientId, amount, orderId);

        try {
            billDecrease(clientId, amount, orderId);
            sendBillStatusEvent(clientId, orderId, SUCCESS_STATUS);
        } catch (IllegalArgumentException e) {
            sendBillStatusEvent(clientId, orderId, FAILURE_STATUS);
            throw e;
        }
    }

    private void billDecrease(String clientId, long amount, String orderId) {
        Bill bill = billRepository.getBillBalanceWithLock(clientId);
        if (bill == null || (bill.getBalance() - amount) < 0) {
            throw new IllegalArgumentException("The given amount greater then current balance");
        }
        billDecreaseRepository.save(new BillDecrease(clientId, amount, orderId));
        billRepository.setBillBalance((-1) * amount, clientId, LocalDateTime.now());
    }

    private void sendBillStatusEvent(String clientId, String orderId, String status) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(BILLS_TOPIC_NAME, orderId, status);
        producerRecord.headers().add(KAFKA_CLIENT_HEADER, clientId.getBytes(StandardCharsets.UTF_8));
        producer.send(producerRecord);
    }

    @Override
    public long getBalance(String clientId) {
        checkClientId(clientId);
        Optional<Bill> bill = billRepository.findById(clientId);
        if (bill.isEmpty()) {
            throw new IllegalArgumentException("The given client doesn't exist in billing");
        }
        return bill.get().getBalance();
    }

    private void checkRequestData(String clientId, Long amount, String orderId) {
        checkRequestData(clientId, amount);
        Assert.hasText(orderId, "The given orderId must not be empty");
    }

    private void checkRequestData(String clientId, Long amount) {
        checkClientId(clientId);
        Assert.notNull(amount, "The given amount must not be null");
        Assert.isTrue(amount > 0, "The given amount must be greater than zero");
    }

    private void checkClientId(String clientId) {
        Assert.hasText(clientId, "The given client must not be empty");
    }
}
