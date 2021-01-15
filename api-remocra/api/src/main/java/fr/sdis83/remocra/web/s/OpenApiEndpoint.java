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

@Path("/openapi")
public class OpenApiEndpoint extends BaseOpenApiResource {
@Context
ServletConfig config;

@Context
Application app;

@GET
@Path("openapi.{type:json|yaml}")
@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
@Operation(hidden = true)
@PermitAll
public Response getOpenApi(@Context HttpHeaders headers,
                           @Context UriInfo uriInfo,
                           @PathParam("type") String type) throws Exception {
        return super.getOpenApi(headers, config, app, uriInfo, type);
}

@GET
@Produces(MediaType.TEXT_HTML)
@Operation(hidden = true)
@PermitAll
public Response showOpenApi() {
        return Response.ok()
                .entity(
                        "\n"
                                + "<!DOCTYPE html>\n"
                                + "<html>\n"
                                + "<head>\n"
                                + "  <meta charset=\"UTF-8\">\n"
                                + "  <title>Bee technologies - API</title>\n"
                                + "  <link rel=\"stylesheet\" type=\"text/css\""
                                + " href=\"//unpkg.com/swagger-ui-dist@3/swagger-ui.css\">\n"
                                + "  <script"
                                + " src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\"></script>\n"
                                + "  <script"
                                + " src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-standalone-preset.js\"></script>\n"
                                + "</head>\n"
                                + "<body>\n"
                                + "  <script>\n"
                                + "window.onload = function() {\n"
                                + "const ui = SwaggerUIBundle({\n"
                                + "    url: \"openapi/openapi.yaml\",\n"
                                + "    dom_id: '#swagger-ui',\n"
                                + "    presets: [\n"
                                + "      SwaggerUIBundle.presets.apis,\n"
                                + "      SwaggerUIBundle.SwaggerUIStandalonePreset\n"
                                + "    ],\n"
                                + "  })\n"
                                + "}\n"
                                + "</script>\n"
                                + "<div id=\"swagger-ui\">\n"
                                + "</body>\n"
                                + "</html>\n")
                .build();
}

}
