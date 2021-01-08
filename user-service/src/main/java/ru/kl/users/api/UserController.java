package ru.kl.users.api;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.kl.users.model.User;
import ru.kl.users.repository.UserRepository;

import static ru.kl.users.api.AuthorizationVerifying.GET_BY_ID_REQUEST;
import static ru.kl.users.api.AuthorizationVerifying.INSERT_REQUEST;
import static ru.kl.users.api.AuthorizationVerifying.UPDATE_REQUEST;
import static ru.kl.users.api.AuthorizationVerifying.checkAuthorization;
import static ru.kl.users.configuration.KafkaProducerConfig.USERS_TOPIC_NAME;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private static final String AUTH_ROLES_HEADER = "X-Auth-Roles";

    private static final String AUTH_CLIENT_HEADER = "X-Auth-Client";

    private final UserRepository userRepository;

    private final KafkaTemplate<String, String> producer;

    @GetMapping("/{client}")
    public Mono<User> getById(
            @RequestHeader(value = AUTH_ROLES_HEADER) String authRoles,
            @RequestHeader(value = AUTH_CLIENT_HEADER) String authClient,
            @PathVariable("client") String client
    ) {
        Assert.hasText(client, "The given client must not be empty");
        checkAuthorization(GET_BY_ID_REQUEST, authRoles, authClient, client);
        return userRepository.findById(client)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The client is not found")));
    }

    @PutMapping
    public Mono<User> insert(
            @RequestHeader(value = AUTH_ROLES_HEADER) String authRoles,
            @RequestBody String client
    ) {
        Assert.hasText(client, "The given client must not be empty");
        checkAuthorization(INSERT_REQUEST, authRoles, null, null);
        return userRepository.insert(new User(client, null))
                .doOnSuccess(this::sendCreateOrderEvent);
    }

    @PostMapping
    public Mono<User> update(
            @RequestHeader(value = AUTH_ROLES_HEADER) String authRoles,
            @RequestHeader(value = AUTH_CLIENT_HEADER) String authClient,
            @RequestBody User user
    ) {
        Assert.notNull(user, "The given user must not be null");
        checkAuthorization(UPDATE_REQUEST, authRoles, authClient, user.getClient());
        return userRepository.save(user);
    }

    private void sendCreateOrderEvent(User user) {
        producer.send(USERS_TOPIC_NAME, user.getClient());
    }
}
