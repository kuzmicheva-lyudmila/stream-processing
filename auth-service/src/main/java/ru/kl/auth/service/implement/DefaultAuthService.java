package ru.kl.auth.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import ru.kl.auth.model.ClientDetails;
import ru.kl.auth.model.Headers;
import ru.kl.auth.model.Role;
import ru.kl.auth.model.auth.AuthRequest;
import ru.kl.auth.model.auth.AuthResponse;
import ru.kl.auth.service.AuthService;
import ru.kl.auth.service.ClientService;
import ru.kl.auth.service.JWTService;
import ru.kl.auth.service.PasswordEncoderService;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final ClientService clientService;

    private final PasswordEncoderService passwordEncoderService;

    private final JWTService jwtService;

    @Override
    public Mono<Void> signUp(AuthRequest authRequest) {
        checkAuthRequest(authRequest);
        return clientService.register(
                new ClientDetails(
                        authRequest.getEmail(),
                        passwordEncoderService.encode(authRequest.getPassword()),
                        Set.of(Role.USER)
                )
        );
    }

    @Override
    public Mono<AuthResponse> signIn(AuthRequest authRequest) {
        checkAuthRequest(authRequest);
        return clientService.get(authRequest.getEmail())
                .filter(
                        clientDetails
                                -> passwordEncoderService.verify(authRequest.getPassword(), clientDetails.getSecret())
                )
                .flatMap(clientDetails -> Mono.just(
                        new AuthResponse(BEARER_PREFIX + jwtService.generateToken(clientDetails)))
                )
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Map<String, String>> authenticate(String token) {
        checkAuthToken(token);
        return Mono.just(token.substring(BEARER_PREFIX.length()))
                .filter(jwtService::validateToken)
                .map(
                        t -> Map.of(
                                Headers.AUTH_CLIENT_HEADER_NAME, jwtService.getClientNameFromToken(t),
                                Headers.AUTH_ROLES_HEADER_NAME, jwtService.getRolesFromToken(t)
                        )
                )
                .switchIfEmpty(Mono.empty());
    }

    private void checkAuthRequest(AuthRequest authRequest) {
        Assert.notNull(authRequest, "The given authRequest must not be null");
        Assert.hasText(authRequest.getEmail(), "The given email must not be empty");
        Assert.hasText(authRequest.getPassword(), "The given password must not be empty");
    }

    private void checkAuthToken(String token) {
        Assert.hasText(token, "The given token must not be empty");
        if (!token.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("The given token requests BEARER");
        }
    }
}
