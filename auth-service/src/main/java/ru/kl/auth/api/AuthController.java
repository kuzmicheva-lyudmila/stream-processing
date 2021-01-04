package ru.kl.auth.api;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.kl.auth.model.Headers;
import ru.kl.auth.model.auth.AuthRequest;
import ru.kl.auth.model.auth.AuthResponse;
import ru.kl.auth.service.AuthService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<Void>> signUp(@RequestBody AuthRequest authRequest) {
        return authService.signUp(authRequest)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<AuthResponse>> signIn(@RequestBody AuthRequest authRequest) {
        return authService.signIn(authRequest)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @GetMapping
    public Mono<ResponseEntity<String>> authenticate(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return authService.authenticate(token)
                .map(
                        m -> {
                            HttpHeaders headers = new HttpHeaders();
                            headers.set(Headers.AUTH_CLIENT_HEADER_NAME, m.get(Headers.AUTH_CLIENT_HEADER_NAME));
                            headers.set(Headers.AUTH_ROLES_HEADER_NAME, m.get(Headers.AUTH_ROLES_HEADER_NAME));
                            return ResponseEntity.ok().headers(headers).body("success");
                        }
                )
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
                .onErrorResume(
                        error -> {
                            if (error instanceof JwtException) {
                                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                            }
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                        }
                );
    }
}
