package org.acme.exception.mapper;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.exception.response.CustomExceptionMessage;
import org.acme.exception.InvalidLoginException;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof InvalidLoginException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new CustomExceptionMessage(exception.getMessage()))
                    .build();
        }

        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new CustomExceptionMessage(exception.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new CustomExceptionMessage("Internal Server Error:" + exception.getMessage()))
                .build();
    }
}
