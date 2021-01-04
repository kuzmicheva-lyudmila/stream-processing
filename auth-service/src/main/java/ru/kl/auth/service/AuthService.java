package ru.kl.auth.service;

import reactor.core.publisher.Mono;
import ru.kl.auth.model.auth.AuthRequest;
import ru.kl.auth.model.auth.AuthResponse;

import java.util.Map;

public interface AuthService {

    Mono<Void> signUp(AuthRequest authRequest);
    Mono<AuthResponse> signIn(AuthRequest authRequest);
    Mono<Map<String, String>> authenticate(String token);
}
