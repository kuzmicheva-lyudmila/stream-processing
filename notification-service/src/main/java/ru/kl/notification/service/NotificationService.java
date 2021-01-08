package ru.kl.notification.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.kl.notification.model.Event;

public interface NotificationService {

    Flux<Event> get(String clientId);

    Mono<Void> create(String clientId, Event event);
}
