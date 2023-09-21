package fr.sdis83.remocra.web.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;
import java.util.Optional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

  @Override
  public Response toResponse(JsonMappingException exception) {
    String msg = (exception.getCause() == null ? exception : exception.getCause()).getMessage();
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(ImmutableExceptionResponse.of(Optional.ofNullable(msg)))
        .build();
  }
}
