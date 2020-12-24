package fr.sdis83.remocra.web.s;

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/openapi.{type:json|yaml}")
public class OpenApiEndpoint extends BaseOpenApiResource {
@Context
ServletConfig config;

@Context
Application app;

@GET
@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
@Operation(hidden = true)
@PermitAll
public Response getOpenApi(@Context HttpHeaders headers,
                           @Context UriInfo uriInfo,
                           @PathParam("type") String type) throws Exception {
        return super.getOpenApi(headers, config, app, uriInfo, type);
}
}
