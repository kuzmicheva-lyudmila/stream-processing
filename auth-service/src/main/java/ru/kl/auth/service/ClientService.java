package ru.kl.auth.service;

import reactor.core.publisher.Mono;
import ru.kl.auth.model.ClientDetails;

public interface ClientService {

    Mono<Void> register(ClientDetails clientDetails);
    Mono<ClientDetails> get(String client);
}
