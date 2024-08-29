package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.bo.UserBO;
import org.acme.model.dto.UserLoginRequestDTO;
import org.acme.model.dto.UserLoginResponseDTO;
import org.acme.model.dto.UserRegisterDTO;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    UserBO userBO;

    @POST
    @Path("/register")
    public Response register(UserRegisterDTO userRegisterDTO){
        userBO.register(userRegisterDTO);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    public Response login(UserLoginRequestDTO userLoginRequestDTO){
        UserLoginResponseDTO response = userBO.login(userLoginRequestDTO);

        return Response.ok(response).build();
    }
}
