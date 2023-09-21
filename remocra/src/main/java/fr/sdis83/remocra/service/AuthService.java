package fr.sdis83.remocra.service;

import fr.sdis83.remocra.security.RemocraAuthenticationProvider;
import javax.naming.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@Configuration
public class AuthService {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private RemocraAuthenticationProvider authProvider;

  public AuthService() {}

  @PreAuthorize("isAuthenticated()")
  public static String getCurrentUserIdentifiant() {
    Object principal = getCurrentAuth().getPrincipal();
    if (principal instanceof String) {
      return (String) principal;
    } else {
      return ((User) principal).getUsername();
    }
  }

  public static Authentication getCurrentAuth() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static Boolean isUserAuthenticated() {
    Authentication currentAuth = getCurrentAuth();

    return currentAuth.isAuthenticated() && !"anonymousUser".equals(currentAuth.getPrincipal());
  }

  /** Logout de l'utilisateur */
  public static void logoutUser() {
    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    SecurityContextHolder.clearContext();
  }

  /**
   * Authentification de l'utilisateur.
   *
   * @param username
   * @param password
   * @return le userName ou null si l'utilisateur n'est pas authentifié
   * @throws AuthenticationException
   */
  public String authUser(String username, String password) {
    logger.debug("Attempting login for user : " + username);

    Authentication aut = null;
    if (username != null && password != null) {
      try {
        aut =
            authProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      } catch (BadCredentialsException badCredException) {
        logger.info("Mauvais mot de passe pour l'utilisateur " + username);
        return null;
      } catch (Exception authException) {
        // Impossible d'authentifier l'utilisateur
        logger.error("Exception occured while loging in user " + username, authException);
        return null;
      }
    }

    if (!aut.isAuthenticated() || "anonymousUser".equals(aut.getPrincipal())) {
      // Utilisateur non authentifié
      logger.error("User " + username + "is not authenticated : ");
      return null;
    }

    // TODO utiliser les exceptions plutot que retourner null : A voir
    // problème quand le premier login fail, par la suite les autres login
    // faillent aussi...
    // Authentication aut = authProvider.authenticate(new
    // UsernamePasswordAuthenticationToken(username, password));
    //
    // if (!aut.isAuthenticated() ||
    // "anonymousUser".equals(aut.getPrincipal())) {
    // throw new AuthenticationException("Anonymous login non autorisé");
    // }

    SecurityContextHolder.getContext().setAuthentication(aut);

    return getCurrentUserIdentifiant();
  }
}
