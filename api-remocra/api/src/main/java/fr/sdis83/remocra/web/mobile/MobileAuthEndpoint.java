package fr.sdis83.remocra.web.mobile;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.repository.ParametreRepository;
import fr.sdis83.remocra.usecase.authn.JWTAuthUser;
import fr.sdis83.remocra.usecase.authn.MobileAuthUser;
import fr.sdis83.remocra.util.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
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

  @Inject MobileAuthUser mobileAuthUser;
  @Inject ParametreRepository parametreRepository;

  @Path("/token")
  @Operation(
      summary = "Check le token",
      tags = {GlobalConstants.REMOCRA_MOBILE_TAG},
      hidden = true)
  @PUT
  @AuthDevice
  public Response checkToken() {
    // On fait rien, @AuthDevice fait le travail
    return Response.ok().build();
  }

  @Path("/check")
  @Operation(
      summary = "Check url et envoie le mot de passe admin",
      tags = {GlobalConstants.REMOCRA_MOBILE_TAG},
      hidden = true)
  @PUT
  @PermitAll
  public Response checkUrl() {
    return Response.ok(
            parametreRepository
                .getParametre(GlobalConstants.PARAMETRE_MDP_ADMINISTRATEUR)
                .getValeurParametre())
        .build();
  }

  @Path("/login")
  @Operation(
      summary = "Permet de se connecter à l'application mobile",
      tags = {GlobalConstants.REMOCRA_MOBILE_TAG},
      hidden = true)
  @POST
  @PermitAll
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response authenticateMobile(
      @FormParam("username") @NotNull String username,
      @FormParam("password") @NotNull String password,
      @FormParam("versionName") @NotNull String versionName)
      throws Exception {
    JWTAuthUser.Response res = mobileAuthUser.authenticateMobile(username, password, versionName);
    if (res.status() == JWTAuthUser.Status.OK) {
      return Response.ok(
              new LoginResponse(
                  username,
                  res.token().get(),
                  res.dateProchaineDeconnexion().isPresent()
                      ? res.dateProchaineDeconnexion().get()
                      : null))
          .build();
    }
    throw new NotAuthorizedException("Unauthorized");
  }

  static class LoginResponse {
    public final String username;
    public final String token;
    public final String dateProchaineDeconnexion;

    public LoginResponse(String username, String token, String dateProchaineDeconnexion) {
      this.username = username;
      this.token = token;
      this.dateProchaineDeconnexion = dateProchaineDeconnexion;
    }
  }
}
