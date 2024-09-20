package org.acme.controller;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.bo.AuditLogBO;
import org.acme.model.bo.JwtValidateBO;
import org.acme.model.bo.UserBO;
import org.acme.model.dto.UserContactDTO;
import org.acme.model.dto.UserLoginRequestDTO;
import org.acme.model.dto.UserLoginResponseDTO;
import org.acme.model.dto.UserRegisterDTO;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserBO userBO;

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JwtValidateBO jwtValidateBO;

    @POST
    @Path("/register")
    public Response register(@Valid UserRegisterDTO userRegisterDTO){
        auditLogBO.logToDatabase("REGISTER_REQUEST", userRegisterDTO.getEmail(), LocalDateTime.now(),UserController.class);
        userBO.register(userRegisterDTO);
        auditLogBO.logToDatabase("REGISTER_REQUEST_SUCCESS", userRegisterDTO.getEmail(), LocalDateTime.now(),UserController.class);
        return Response.ok().build();
    }

    @GET
    @Path("/verify-email/{email}")
    public Response verifyEmail(@PathParam("email") String email){
        auditLogBO.logToDatabase("VERIFY_EMAIL_REQUEST", email, LocalDateTime.now(),UserController.class);
        boolean isExist = userBO.verifyEmail(email);
        auditLogBO.logToDatabase("VERIFY_EMAIL_REQUEST_SUCCESS", email, LocalDateTime.now(),UserController.class);
        return Response.ok(isExist).build();
    }

    @POST
    @Path("/login")
    public Response login(UserLoginRequestDTO userLoginRequestDTO){
        auditLogBO.logToDatabase("LOGIN_REQUEST", userLoginRequestDTO.getEmail(), LocalDateTime.now(),UserController.class);
        UserLoginResponseDTO response = userBO.login(userLoginRequestDTO);
        auditLogBO.logToDatabase("LOGIN_REQUEST_SUCCESS", userLoginRequestDTO.getEmail(), LocalDateTime.now(),UserController.class);
        return Response.ok(response).build();
    }

    @GET
    @Path("/contactsByUserId/{id}")
    public Response contactsByUserId(@HeaderParam("Authorization") String token, @PathParam("id") Long id) throws ParseException {
        String email = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("CONTACTS_BY_USER_ID_REQUEST", email, LocalDateTime.now(),UserController.class);
        List<UserContactDTO> response = userBO.contactsByUserId(id);
        auditLogBO.logToDatabase("CONTACTS_BY_USER_ID_REQUEST_SUCCESS", email, LocalDateTime.now(),UserController.class);
        return Response.ok(response).build();

    }

    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@HeaderParam("Authorization") String token,@PathParam("email") String email){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("GET_USER_BY_EMAIL_REQUEST", emailToken, LocalDateTime.now(),UserController.class);
        var response = userBO.getUserByEmail(email, emailToken);
        auditLogBO.logToDatabase("GET_USER_BY_EMAIL_REQUEST_SUCCESS", emailToken, LocalDateTime.now(),UserController.class);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{userId}/groups")
    public Response getUserGroups(@HeaderParam("Authorization") String token,@PathParam("userId")Long userId){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("GET_USER_GROUPS_REQUEST", emailToken, LocalDateTime.now(),UserController.class);
        var response = userBO.getUserGroups(userId);
        auditLogBO.logToDatabase("GET_USER_GROUPS_REQUEST_SUCCESS", emailToken, LocalDateTime.now(),UserController.class);
        return Response.ok(userBO.getUserGroups(userId)).build();
    }
}
