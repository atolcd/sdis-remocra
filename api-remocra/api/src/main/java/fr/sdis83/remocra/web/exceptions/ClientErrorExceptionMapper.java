package fr.sdis83.remocra.web.exceptions;

import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

@Override
public Response toResponse(ClientErrorException exception) {
        int status = exception.getResponse().getStatus();
        return Response.status(status).type(MediaType.APPLICATION_JSON)
               .entity(ImmutableExceptionResponse.of(Optional.ofNullable(exception.getMessage()))).build();
}
}
