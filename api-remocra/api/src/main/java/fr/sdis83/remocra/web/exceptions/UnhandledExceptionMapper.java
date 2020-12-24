package fr.sdis83.remocra.web.exceptions;

import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Optional;

/**
 * Workaround https://issues.jboss.org/browse/RESTEASY-1006.
 */
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {
private static final Logger logger = LoggerFactory.getLogger(UnhandledExceptionMapper.class);

// XXX: What about exceptions in endpoints who return JSON data?

@Override
public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
                Response response = ((WebApplicationException) exception).getResponse();
                if (response != null) {
                        return response;
                }
        }

        logger.error("Unhandled exception: {}", exception.getMessage(), exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
               .entity(ImmutableExceptionResponse.of(Optional.ofNullable(exception.getMessage()))).build();
}
}
