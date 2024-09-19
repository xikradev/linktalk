package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.bo.AuditLogBO;
import org.acme.model.bo.JwtValidateBO;
import org.acme.model.bo.MessageBO;
import org.acme.model.dto.MessageResponseDTO;
import org.acme.model.dto.MessageUpdateRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

@Path("/message")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageController {

    @Inject
    MessageBO messageBO;
    @Inject
    JwtValidateBO jwtValidateBO;
    @Inject
    AuditLogBO auditLogBO;

    @GET
    @Path("/conversation/{conversationId}")
    public Response getConversationMessages(@HeaderParam("Authorization") String token,@PathParam("conversationId") Long conversationId){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("GET_CONVERSATION_MESSAGES_REQUEST", emailToken, LocalDateTime.now(),MessageController.class);
        List<MessageResponseDTO> response = messageBO.getConversationMessages(conversationId);
        auditLogBO.logToDatabase("GET_CONVERSATION_MESSAGES_REQUEST_SUCCESS", emailToken, LocalDateTime.now(),MessageController.class);
        return Response.ok(response).build();
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupMessages(@HeaderParam("Authorization") String token,@PathParam("groupId") Long groupId){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("GET_GROUP_MESSAGES_REQUEST", emailToken, LocalDateTime.now(),MessageController.class);
        List<MessageResponseDTO> response = messageBO.getGroupMessages(groupId);
        auditLogBO.logToDatabase("GET_GROUP_MESSAGES_REQUEST", emailToken, LocalDateTime.now(),MessageController.class);
        return Response.ok(response).build();
    }


    @DELETE
    @Path("/{messageId}")
    public Response deleteMessageById(@PathParam("messageId") Long messageId){
        try {
            messageBO.deleteMessageById(messageId);
            return Response.ok("Mensagem deletada com sucesso").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

}
