package ru.kl.auth.service;

import ru.kl.auth.model.ClientDetails;

public interface JWTService {

    String generateToken(ClientDetails clientDetails);
    Boolean validateToken(String token);
    String getClientNameFromToken(String token);
    String getRolesFromToken(String token);
}
