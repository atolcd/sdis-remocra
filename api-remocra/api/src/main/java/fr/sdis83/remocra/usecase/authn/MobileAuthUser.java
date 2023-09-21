package fr.sdis83.remocra.usecase.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.CompatibleVersions;
import fr.sdis83.remocra.repository.UtilisateurModel;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import org.immutables.value.Value;

@Value.Enclosing
public class MobileAuthUser {

  private final LdapUsecase ldapUsecase;
  private final UtilisateursRepository utilisateursRepository;
  private final AuthCommun authCommun;

  private final CompatibleVersions compatibleVersions;

  @Inject
  public MobileAuthUser(
      LdapUsecase ldapUsecase,
      UtilisateursRepository utilisateursRepository,
      AuthCommun authCommun,
      CompatibleVersions compatibleVersions) {

    this.ldapUsecase = ldapUsecase;
    this.utilisateursRepository = utilisateursRepository;
    this.authCommun = authCommun;
    this.compatibleVersions = compatibleVersions;
  }

  public ImmutableJWTAuthUser.Response authenticateMobile(
      String username, String password, String versionName) throws Exception {

    // Avant tout, on check si la version correspond, sinon on empêche la connexion
    compatibleVersions.checkCompat(versionName);

    // On cherche l'utilisateur en base
    UtilisateurModel user = utilisateursRepository.getByUsername(username);

    // Utilisateur non trouvé en base
    if (user == null) {
      return ImmutableJWTAuthUser.Response.builder().status(JWTAuthUser.Status.NOT_FOUND).build();
    }

    // Verify user is active
    if (!user.actif()) {
      throw new Exception("User " + username + " is not active in the database.");
    }
    if (ldapUsecase.ldapAuthentication(username, password)) {
      // On s'est connecté à LDAP => on a trouvé l'utilisateur et il avait le bon mot de passe !
      return ImmutableJWTAuthUser.Response.builder()
          .status(JWTAuthUser.Status.OK)
          .token(authCommun.generateToken(username))
          .build();
    }

    // Sinon, on essaie la connexion non LDAP
    String encodedPassword = authCommun.encodePassword(password, user.salt());

    if (!user.password().equals(encodedPassword)) {
      return ImmutableJWTAuthUser.Response.builder()
          .status(JWTAuthUser.Status.BAD_CREDENTIALS)
          .build();
    }

    return ImmutableJWTAuthUser.Response.builder()
        .status(JWTAuthUser.Status.OK)
        .token(authCommun.generateToken(username))
        .build();
  }
}
