package ru.kl.auth.service.implement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.kl.auth.configuration.UserServiceClientProperties;
import ru.kl.auth.model.ClientDetails;
import ru.kl.auth.model.Headers;
import ru.kl.auth.model.Role;
import ru.kl.auth.repository.ClientRepository;
import ru.kl.auth.service.ClientService;

@Service
@Slf4j
public class DefaultClientService implements ClientService {

    private final ClientRepository clientRepository;

    private final WebClient userServiceClient;

    public DefaultClientService(ClientRepository clientRepository, UserServiceClientProperties properties) {
        this.clientRepository = clientRepository;

        this.userServiceClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(Headers.AUTH_ROLES_HEADER_NAME, Role.ADMIN.name())
                .build();
    }

    @Override
    public Mono<Void> register(ClientDetails clientDetails) {
        return Mono.zip(clientRepository.save(clientDetails), registerUser(clientDetails.getClient())).then();
    }

    @Override
    public Mono<ClientDetails> get(String client) {
        return clientRepository.findById(client);
    }

    private Mono<Void> registerUser(String client) {
        return userServiceClient.put()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("User: {}", response))
                .doOnError(error -> log.error("Error: ", error))
                .then();
    }
}
