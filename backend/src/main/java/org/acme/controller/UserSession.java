package org.acme.controller;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.acme.model.bo.UserBO;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class UserSession {

    @Context
    HttpHeaders httpHeaders;
    @Inject
    JWTParser jwtParser;

    @Inject
    UserBO userBO;

    public String getCurrentUser() {
        // Extrair o token do cabeçalho Authorization
        String authHeader = httpHeaders.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT ausente ou inválido");
        }

        // Remover o prefixo "Bearer " e obter o token
        String token = authHeader.substring(7);

        // Decodificar o token e extrair o nome do usuário
        return extractUsernameFromToken(token);
    }

    private String extractUsernameFromToken(String token) {
        // Decodificar o JWT e retornar o nome do usuário
        // Supondo que você use uma biblioteca como o io.jsonwebtoken (jjwt)
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            Object userNameClaim = jwt.getClaim("userName");
            String userName = userNameClaim.toString();


            return userName;
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido", e);
        }
    }
}
