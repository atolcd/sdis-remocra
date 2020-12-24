package fr.sdis83.remocra.web.exceptions;

import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Optional;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

@Override
public Response toResponse(ValidationException exception) {
        String msg = exception.getMessage();
        if (exception instanceof ResteasyViolationException) {
                List<ResteasyConstraintViolation> violations = ((ResteasyViolationException) exception).getViolations();
                if (!violations.isEmpty()) {
                        msg = violations.get(0).getMessage();
                }
        }

        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
               .entity(ImmutableExceptionResponse.of(Optional.ofNullable(msg))).build();
}
}
