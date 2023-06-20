package fr.sdis83.remocra.usecase.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import fr.sdis83.remocra.repository.UtilisateurModel;
import org.immutables.value.Value;

@Value.Enclosing
public class MobileAuthUser {

    private final LdapUsecase ldapUsecase;
    private final UtilisateursRepository utilisateursRepository;
    private final AuthCommun authCommun;

    @Inject
    public MobileAuthUser(LdapUsecase ldapUsecase, UtilisateursRepository utilisateursRepository, AuthCommun authCommun) {
        this.ldapUsecase = ldapUsecase;
        this.utilisateursRepository = utilisateursRepository;
        this.authCommun = authCommun;
    }

    public ImmutableJWTAuthUser.Response authenticateMobile(String username, String password) throws Exception {

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
            return ImmutableJWTAuthUser.Response.builder().status(JWTAuthUser.Status.BAD_CREDENTIALS).build();
        }

        return ImmutableJWTAuthUser.Response.builder()
                .status(JWTAuthUser.Status.OK)
                .token(authCommun.generateToken(username))
                .build();
    }
}
