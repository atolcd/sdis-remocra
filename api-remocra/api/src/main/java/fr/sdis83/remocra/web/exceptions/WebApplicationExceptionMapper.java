package fr.sdis83.remocra.web.exceptions;

import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;

import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

@Override
public Response toResponse(WebApplicationException exception) {
        if (exception instanceof RedirectionException) {
                return exception.getResponse();
        }
        int status = exception.getResponse().getStatus();
        return Response.status(status).type(MediaType.APPLICATION_JSON)
               .entity(ImmutableExceptionResponse.of(Optional.ofNullable(exception.getMessage()))).build();
}
}
