package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.model.bo.GroupBO;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.dto.GroupResponseDTO;

import java.util.List;

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupController {

    @Inject
    GroupBO groupBO;

    @Inject
    UriInfo uriInfo;

    @POST
    public Response createGroup(@QueryParam("userIds")List<Long> userIds, GroupRequestDTO groupRequestDTO){
        if (userIds == null || userIds.isEmpty()) {
            throw new BadRequestException("é preciso informar pelo menos id de um usuário para criar o grupo");
        }
        GroupResponseDTO response = groupBO.createGroup(userIds,groupRequestDTO);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(Long.toString(response.getId()));
        return Response.created(uriBuilder.build()).build();
    }
}
