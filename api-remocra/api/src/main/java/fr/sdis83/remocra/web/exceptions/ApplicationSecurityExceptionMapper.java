package fr.sdis83.remocra.web.exceptions;

import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Provider
public class ApplicationSecurityExceptionMapper implements ExceptionMapper<ApplicationSecurityException> {

@Override
public Response toResponse(ApplicationSecurityException exception) {
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
               .entity(ImmutableExceptionResponse.of(Optional.ofNullable(exception.getMessage()))).build();
}
}
