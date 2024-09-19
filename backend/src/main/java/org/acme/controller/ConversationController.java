package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.model.bo.AuditLogBO;
import org.acme.model.bo.ConversationBO;
import org.acme.model.bo.JwtValidateBO;
import org.acme.model.entity.Conversation;

import java.time.LocalDateTime;

@Path("/conversation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversationController {
    @Inject
    ConversationBO conversationBO;

    @Inject
    UriInfo uriInfo;

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JwtValidateBO jwtValidateBO;

    @POST
    public Response startConversation(@HeaderParam("Authorization") String token,@QueryParam("user1Id") Long user1Id,@QueryParam("user2Id") Long user2Id){
        String emailToken = jwtValidateBO.validateToken(token);
        auditLogBO.logToDatabase("START_CONVERSATION_REQUEST", emailToken, LocalDateTime.now(), ConversationController.class);
        Conversation conversation = conversationBO.startConversation(user1Id, user2Id,emailToken);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(Long.toString(conversation.getId()));
        auditLogBO.logToDatabase("START_CONVERSATION_REQUEST_SUCCESS", emailToken, LocalDateTime.now(), ConversationController.class);
        return Response.created(uriBuilder.build()).build();
    }
}
