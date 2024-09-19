package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.model.bo.AuditLogBO;
import org.acme.model.bo.GroupBO;
import org.acme.model.bo.JwtValidateBO;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.dto.GroupResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupController {

    @Inject
    GroupBO groupBO;

    @Inject
    UriInfo uriInfo;

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JwtValidateBO jwtValidateBO;

    @POST
    public Response createGroup(@HeaderParam("Authorization") String token,@QueryParam("userIds")List<Long> userIds, GroupRequestDTO groupRequestDTO){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("CREATE_GROUP_REQUEST", emailToken, LocalDateTime.now(),GroupController.class);
        if (userIds == null || userIds.isEmpty()) {
            auditLogBO.logToDatabase("CREATE_GROUP_REQUEST_FAILED_NOT_FOUND_USERS_IDS", emailToken, LocalDateTime.now(),GroupController.class);
            throw new BadRequestException("é preciso informar pelo menos id de um usuário para criar o grupo");
        }
        GroupResponseDTO response = groupBO.createGroup(userIds,groupRequestDTO, emailToken);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(Long.toString(response.getId()));
        auditLogBO.logToDatabase("CREATE_GROUP_REQUEST_SUCCESS", emailToken, LocalDateTime.now(),GroupController.class);
        return Response.created(uriBuilder.build()).build();
    }
}
