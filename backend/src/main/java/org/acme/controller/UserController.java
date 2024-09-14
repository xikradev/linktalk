package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.bo.UserBO;
import org.acme.model.dto.UserLoginRequestDTO;
import org.acme.model.dto.UserLoginResponseDTO;
import org.acme.model.dto.UserRegisterDTO;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserBO userBO;

    @POST
    @Path("/register")
    public Response register(@Valid UserRegisterDTO userRegisterDTO){
        userBO.register(userRegisterDTO);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    public Response login(UserLoginRequestDTO userLoginRequestDTO){
        UserLoginResponseDTO response = userBO.login(userLoginRequestDTO);

        return Response.ok(response).build();
    }

    @GET
    @Path("/contactsByUserId/{id}")
    public Response contactsByUserId(@PathParam("id") Long id){
        return Response.ok(userBO.contactsByUserId(id)).build();
    }

    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email){
        return Response.ok(userBO.getUserByEmail(email)).build();
    }
}
