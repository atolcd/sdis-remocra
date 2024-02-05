package fr.sdis83.remocra.usecase.authn;

import static fr.sdis83.remocra.util.GlobalConstants.PARAMETRE_DUREE_VALIDITE_TOKEN;
import static fr.sdis83.remocra.util.GlobalConstants.PARAMETRE_MODE_DECONNECTE;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.CompatibleVersions;
import fr.sdis83.remocra.repository.ParametreRepository;
import fr.sdis83.remocra.repository.UtilisateurModel;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import fr.sdis83.remocra.usecase.utils.DateUtils;
import java.time.Duration;
import java.time.Instant;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value.Enclosing
public class MobileAuthUser {

  private final LdapUsecase ldapUsecase;
  private final UtilisateursRepository utilisateursRepository;
  private final ParametreRepository parametreRepository;
  private final AuthCommun authCommun;

  private final CompatibleVersions compatibleVersions;

  private static final Logger logger = LoggerFactory.getLogger(MobileAuthUser.class);

  @Inject
  public MobileAuthUser(
      LdapUsecase ldapUsecase,
      UtilisateursRepository utilisateursRepository,
      AuthCommun authCommun,
      CompatibleVersions compatibleVersions,
      ParametreRepository parametreRepository) {

    this.ldapUsecase = ldapUsecase;
    this.utilisateursRepository = utilisateursRepository;
    this.authCommun = authCommun;
    this.compatibleVersions = compatibleVersions;
    this.parametreRepository = parametreRepository;
  }

  public ImmutableJWTAuthUser.Response authenticateMobile(
      String username, String password, String versionName) throws Exception {

    logger.trace("Vérification de compatibilité de la version");
    // Avant tout, on check si la version correspond, sinon on empêche la connexion
    compatibleVersions.checkCompat(versionName);

    logger.trace("Connexion de l'utilisateur " + username);
    // On cherche l'utilisateur en base
    UtilisateurModel user = utilisateursRepository.getByUsername(username);

    // Utilisateur non trouvé en base
    if (user == null) {
      logger.error("Utilisateur non présent en base : " + username);
      return ImmutableJWTAuthUser.Response.builder().status(JWTAuthUser.Status.NOT_FOUND).build();
    }

    // Verify user is active
    if (!user.actif()) {
      logger.error("Utilisateur non actif en base : " + username);
      throw new Exception("User " + username + " is not active in the database.");
    }
    if (ldapUsecase.ldapAuthentication(username, password)) {
      // On s'est connecté à LDAP => on a trouvé l'utilisateur et il avait le bon mot de passe !
      return ImmutableJWTAuthUser.Response.builder()
          .status(JWTAuthUser.Status.OK)
          .token(
              authCommun.generateToken(
                  username,
                  Integer.parseInt(
                      parametreRepository
                          .getParametre(PARAMETRE_DUREE_VALIDITE_TOKEN)
                          .getValeurParametre())))
          .build();
    }

    // Sinon, on essaie la connexion non LDAP
    String encodedPassword = authCommun.encodePassword(password, user.salt());

    if (!user.password().equals(encodedPassword)) {
      logger.error("Mauvais mot de passe pour l'utilisateur : " + username);
      return ImmutableJWTAuthUser.Response.builder()
          .status(JWTAuthUser.Status.BAD_CREDENTIALS)
          .build();
    }

    logger.trace(
        "L'utilisateur " + username + " est connecté (" + DateUtils.format(Instant.now()) + ")");

    // On vérifie si le mode "déconnexion" est à true, on envoie la date de la prochaine déconnexion
    boolean accepteModeDeconnecte =
        Boolean.parseBoolean(
            parametreRepository.getParametre(PARAMETRE_MODE_DECONNECTE).getValeurParametre());
    int dureeSession =
        Integer.parseInt(
            parametreRepository.getParametre(PARAMETRE_DUREE_VALIDITE_TOKEN).getValeurParametre());
    if (accepteModeDeconnecte) {
      String date = DateUtils.format(Instant.now().plus(Duration.ofHours(dureeSession)));
      logger.trace("Mode déconnecté autorisé, la session est valable jusqu'au : " + date);
      return ImmutableJWTAuthUser.Response.builder()
          .status(JWTAuthUser.Status.OK)
          .token(authCommun.generateToken(username, dureeSession))
          .dateProchaineDeconnexion(date)
          .build();
    }
    return ImmutableJWTAuthUser.Response.builder()
        .status(JWTAuthUser.Status.OK)
        .token(authCommun.generateToken(username, dureeSession))
        .build();
  }
}
