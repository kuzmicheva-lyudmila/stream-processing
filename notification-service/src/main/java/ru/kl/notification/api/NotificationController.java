package ru.kl.notification.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.kl.notification.model.Event;
import ru.kl.notification.service.NotificationService;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final String AUTH_CLIENT_HEADER = "X-Auth-Client";

    private final NotificationService notificationService;

    @GetMapping
    public Flux<Event> get(@RequestHeader(value = AUTH_CLIENT_HEADER) String clientId) {
        return notificationService.get(clientId);
    }
}
