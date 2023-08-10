package fr.sdis83.remocra.web.mobile;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.usecase.authn.JWTAuthUser;
import fr.sdis83.remocra.usecase.authn.MobileAuthUser;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/mobile/authentication")
@Produces("application/json; charset=UTF-8")
public class MobileAuthEndpoint {

  @Inject
  MobileAuthUser mobileAuthUser;

  @Path("/token")
  @PUT
  @AuthDevice
  public Response checkToken() {
    // On fait rien, @AuthDevice fait le travail
    return Response.ok().build();
  }

  @Path("/check")
  @PUT
  @PermitAll
  public Response checkUrl() {
    return Response.ok().build();
  }


  @Path("/login")
  @POST
  @PermitAll
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response authenticateMobile(
      @FormParam("username") @NotNull String username,
      @FormParam("password") @NotNull String password,
      @FormParam("versionName") @NotNull String versionName
  ) throws Exception {
    JWTAuthUser.Response res = mobileAuthUser.authenticateMobile(username, password, versionName);
    if (res.status() == JWTAuthUser.Status.OK) {
      return Response.ok(
          new LoginResponse(username, res.token().get())
      ).build();
    }
    throw new NotAuthorizedException("Unauthorized");
  }

  static class LoginResponse {
    public final String username;
    public final String token;

    public LoginResponse(String username, String token) {
      this.username = username;
      this.token = token;
    }
  }
}
