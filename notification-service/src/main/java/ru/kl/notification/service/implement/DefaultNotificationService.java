package ru.kl.notification.service.implement;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.kl.notification.model.Event;
import ru.kl.notification.service.NotificationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultNotificationService implements NotificationService {

    private final ConcurrentHashMap<String, List<Event>> notifications = new ConcurrentHashMap<>();

    @Override
    public Flux<Event> get(String clientId) {
        checkClientId(clientId);
        return Flux.fromIterable(notifications.getOrDefault(clientId, Collections.emptyList()));
    }

    @Override
    public Mono<Void> create(String clientId, Event event) {
        checkClientId(clientId);
        Assert.notNull(event, "The given event must not be null");
        saveNotification(clientId, event);
        return Mono.empty();
    }

    private void saveNotification(String clientId, Event event) {
        notifications.putIfAbsent(clientId, new ArrayList<>());
        notifications.get(clientId).add(event);
    }

    private void checkClientId(String clientId) {
        Assert.hasText(clientId, "The given client must not be empty");
    }
}
