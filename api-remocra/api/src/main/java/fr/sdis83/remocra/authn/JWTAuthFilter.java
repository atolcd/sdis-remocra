package fr.sdis83.remocra.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.repository.OrganismesRepository;
import fr.sdis83.remocra.usecase.authn.AuthCommun;
import fr.sdis83.remocra.web.model.ImmutableExceptionResponse;
import fr.sdis83.remocra.web.model.authn.OrganismeModel;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.ArrayUtils;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

  @Inject javax.inject.Provider<OrganismesRepository> organismeRepository;

  @Inject javax.inject.Provider<AuthCommun> authCommunProvider;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    ResourceMethodInvoker methodInvoker =
        (ResourceMethodInvoker)
            requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
    Method method = methodInvoker.getMethod();

    if (method.isAnnotationPresent(PermitAll.class)
        || method.isAnnotationPresent(AuthDevice.class)) {
      return;
    }
    if (method.isAnnotationPresent(DenyAll.class)) {
      requestContext.abortWith(Response.status(403).type(MediaType.APPLICATION_JSON).build());
      return;
    }

    // We consider that role annotations are mandatory on methods
    if (!method.isAnnotationPresent(RolesAllowed.class)) {
      logger.info("Missing RolesAllowed.");
      requestContext.abortWith(Response.status(403).type(MediaType.APPLICATION_JSON).build());
      return;
    }

    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader == null || authorizationHeader.length() < 8) {
      requestContext.abortWith(
          Response.status(401).entity("Authorization header is missing").build());
      return;
    }
    String jws = authorizationHeader.substring("Bearer".length()).trim();

    String username = null;
    String errorMsg = null;
    try {
      username = authCommunProvider.get().readToken(jws);
    } catch (ExpiredJwtException e) {
      errorMsg = "Token has expired";
    } catch (JwtException e) {
      errorMsg = "Token error";
      logger.error(errorMsg, e);
    }
    if (errorMsg != null) {
      requestContext.abortWith(
          Response.status(401)
              .entity(ImmutableExceptionResponse.of(Optional.ofNullable(errorMsg)))
              .build());
      return;
    }

    if (username == null || username.isEmpty()) {
      logger.info("Authentication subject is missing.");
      requestContext.abortWith(
          Response.status(401)
              .entity(
                  ImmutableExceptionResponse.of(
                      Optional.ofNullable("Authentication subject is missing.")))
              .build());
      return;
    }

    OrganismeModel o = organismeRepository.get().readByEmail(username);
    if (o == null) {
      logger.info("Authentication failed for " + username + ".");
      requestContext.abortWith(
          Response.status(401)
              .type(MediaType.APPLICATION_JSON)
              .entity(
                  ImmutableExceptionResponse.of(
                      Optional.ofNullable(username + " account not found.")))
              .build());
      return;
    }

    final UserInfo userInfo =
        ImmutableUserInfo.of(o.getId(), o.getEmail(), o.getRoles(), o.getType(), o.getTypeId());
    final UserPrincipal principal =
        new UserPrincipal() {
          @Override
          public String getName() {
            return userInfo.username();
          }

          @Override
          public Long getUserId() {
            return userInfo.userId();
          }

          @Override
          public UserInfo getUserInfo() {
            return userInfo;
          }

          @Override
          public List<UserRoles> roles() {
            return userInfo.roles();
          }

          @Override
          public String type() {
            return userInfo.type();
          }

          @Override
          public Long typeId() {
            return userInfo.typeId();
          }
        };

    final SecurityContext oldSC = requestContext.getSecurityContext();
    requestContext.setSecurityContext(
        new SecurityContext() {
          @Override
          public Principal getUserPrincipal() {
            return principal;
          }

          @Override
          public boolean isUserInRole(String role) {
            return userInfo.roles().toString().equals(role) || oldSC.isUserInRole(role);
          }

          @Override
          public boolean isSecure() {
            return oldSC.isSecure();
          }

          @Override
          public String getAuthenticationScheme() {
            return null;
          }
        });

    // Si l'organisme utilisateur est administrateur, on bypass tout le reste
    if (userInfo.roles().contains(UserRoles.ADMINISTRER)) {
      return;
    }

    RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
    boolean hasRight = false;
    for (UserRoles r : userInfo.roles()) {
      if (ArrayUtils.contains(rolesAnnotation.value(), r.toString())) {
        hasRight = true;
      }
    }
    if (!hasRight) {
      logger.info("Authorizations failed for {}.", username);
      requestContext.abortWith(Response.status(403).type(MediaType.APPLICATION_JSON).build());
    }
  }
}
