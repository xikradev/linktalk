package org.acme.model.bo;

import io.quarkus.security.UnauthorizedException;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.acme.controller.UserController;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@ApplicationScoped
public class JwtValidateBO {
    @Inject
    JWTParser jwtParser;

    @Inject
    AuditLogBO auditLogBO;

    public String validateToken(String token){
        try{
            JsonWebToken jwt = jwtParser.parse(token);
            if(!jwt.getIssuer().equals("linktalk")){
                auditLogBO.logToDatabase("INVALID_TOKEN", "unknow", LocalDateTime.now(), JwtValidateBO.class);
                throw new UnauthorizedException("Token inválido");
            }
            return jwt.getClaim("email");
        }catch (Exception e){
            auditLogBO.logToDatabase("INVALID_TOKEN", "unknow", LocalDateTime.now(), JwtValidateBO.class);
            throw new UnauthorizedException("Token inválido");
        }
    }
}
