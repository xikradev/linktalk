package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.MessageBO;
import org.acme.model.dto.MessageResponseDTO;

import java.util.List;

@Path("/message")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageController {

    @Inject
    MessageBO messageBO;

    @GET
    public Response getConversationMessages(@QueryParam("conversationId") Long conversationId){
        List<MessageResponseDTO> response = messageBO.getConversationMessages(conversationId);
        return Response.ok(response).build();
    }
}
