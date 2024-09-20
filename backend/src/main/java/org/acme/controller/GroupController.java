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

    @GET
    @Path("/{groupId}/members")
    public Response getGroupMembers(@HeaderParam("Authorization") String token,@PathParam("groupId") Long groupId){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("GET_GROUP_MEMBERS_REQUEST", emailToken, LocalDateTime.now(),GroupController.class);
        try {
            var response = groupBO.getGroupMembers(groupId);
            auditLogBO.logToDatabase("GET_GROUP_MEMBERS_REQUEST_SUCCESS", emailToken, LocalDateTime.now(),GroupController.class);
            return Response.ok(response).build();
        }catch (Exception e){
            auditLogBO.logToDatabase("GET_GROUP_MEMBERS_REQUEST_FAILED_NOT_FOUND_GROUP", emailToken, LocalDateTime.now(),GroupController.class);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


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

    @DELETE
    @Path("/{groupId}")
    public Response removeGroup(@HeaderParam("Authorization") String token, @PathParam("groupId") Long groupId){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("REMOVE_GROUP_REQUEST", emailToken, LocalDateTime.now(), GroupController.class);
        try {
            groupBO.deleteGroupById(groupId);
            auditLogBO.logToDatabase("REMOVE_GROUP_REQUEST_SUCCESS", emailToken, LocalDateTime.now(), GroupController.class);
            return Response.ok("Conversa deletada com sucesso").build();
        }catch (Exception e){
            auditLogBO.logToDatabase("REMOVE_GROUP_REQUEST_FAILED_NOT_FOUND_GROUP", emailToken, LocalDateTime.now(), GroupController.class);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{groupId}/add-user")
    public Response addUserToGroup(@HeaderParam("Authorization") String token,@PathParam("groupId") Long groupId, @QueryParam("userIds")List<Long> userIds) {
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST", emailToken, LocalDateTime.now(), GroupController.class);
        groupBO.addUserToGroup(groupId, userIds, emailToken);
        auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST_SUCCESS", emailToken, LocalDateTime.now(), GroupController.class);
        return Response.ok("Usuário adicionado ao grupo com sucesso").build();
    }

    @PUT
    @Path("/{groupId}/remove-user")
    public Response removeUserFromGroup(@HeaderParam("Authorization") String token,@PathParam("groupId") Long groupId, @QueryParam("userIds")List<Long> userIds) {
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("REMOVE_USER_TO_GROUP_REQUEST", emailToken, LocalDateTime.now(), GroupController.class);
        groupBO.removeUserFromGroup(groupId, userIds, emailToken);
        auditLogBO.logToDatabase("REMOVE_USER_TO_GROUP_REQUEST_SUCCESS", emailToken, LocalDateTime.now(), GroupController.class);
        return Response.ok("Usuário removido do grupo com sucesso").build();
    }

    @PUT
    @Path("/{groupId}/updateName")
    public Response updateGroupName(@HeaderParam("Authorization") String token,@PathParam("groupId") Long groupId, GroupRequestDTO groupRequestDTO){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("UPDATE_GROUP_NAME_REQUEST", emailToken, LocalDateTime.now(), GroupController.class);
        groupBO.updateGroupName(groupId, emailToken, groupRequestDTO);
        auditLogBO.logToDatabase("UPDATE_GROUP_NAME_REQUEST_SUCCESS", emailToken, LocalDateTime.now(), GroupController.class);
        return Response.ok("nome do grupo atualizado com sucesso").build();
    }
}
