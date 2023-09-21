package fr.sdis83.remocra.web.s;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import com.google.inject.Inject;
import fr.sdis83.remocra.usecase.authn.JWTAuthUser;
import fr.sdis83.remocra.web.model.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class JWTAuthEndpoint {

  @Inject JWTAuthUser authUserUseCase;

  @Path("/jwt")
  @POST
  @Operation(
      summary = "Authenticates a user",
      tags = {"Authentication"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a signed JSON Web Token (jwt)",
            headers = {
              @Header(
                  name = AUTHORIZATION,
                  schema = @Schema(type = "string"),
                  description = "Bearer &lt;signed jwt&gt;")
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  @PermitAll
  public Response authenticate(
      @Parameter(description = "Email", required = true) @QueryParam("email") @NotNull String email,
      @Parameter(description = "Password", required = true) @HeaderParam("X-Password") @NotNull
          String password) {
    JWTAuthUser.Response res = authUserUseCase.authenticate(email, password);
    if (res.status() == JWTAuthUser.Status.OK) {
      return Response.ok().header(AUTHORIZATION, "Bearer " + res.token().get()).build();
    }
    throw new NotAuthorizedException("Unauthorized");
  }
}
