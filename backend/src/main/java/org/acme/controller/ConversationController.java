package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.audit.Auditable;
import org.acme.model.bo.ConversationBO;
import org.acme.model.entity.Conversation;

@Path("/conversation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversationController {
    @Inject
    ConversationBO conversationBO;

    @Inject
    UriInfo uriInfo;

    @POST
    public Response startConversation(@QueryParam("user1Id") Long user1Id,@QueryParam("user2Id") Long user2Id){
        Conversation conversation = conversationBO.startConversation(user1Id, user2Id);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(Long.toString(conversation.getId()));
        return Response.created(uriBuilder.build()).build();
    }
}
