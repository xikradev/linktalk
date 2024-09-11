package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.bo.MessageBO;
import org.acme.model.dto.MessageResponseDTO;
import org.acme.model.dto.MessageUpdateRequestDTO;

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

    @PUT
    @Path("/{messageId}")
    public Response updateMessageContent(@PathParam("messageId") Long messageId, MessageUpdateRequestDTO messageUpdateRequestDTO) {
        try {
            messageBO.updateMessageContent(messageId, messageUpdateRequestDTO.getContent());
            return Response.ok("Conteúdo da mensagem atualizado com sucesso").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

}
