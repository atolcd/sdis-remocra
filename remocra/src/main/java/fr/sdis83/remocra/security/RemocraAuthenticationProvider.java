package fr.sdis83.remocra.security;

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Permet d'utiliser la base de données pour la gestion des utilisateurs de
 * l'application.
 */
public class RemocraAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ParamConfService paramConfService;

    public void setParamConfService(ParamConfService paramConfService) {
        this.provider = provider;
    }

    @Autowired
    private ProfileProvider provider;

    @Autowired
    private UtilisateurService utilisateurService;

    public void setProvider(ProfileProvider provider) {
        this.provider = provider;
    }

    @Autowired
    MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    public void setMessageDisgetPasswordEncoder(MessageDigestPasswordEncoder encoder) {
        messageDigestPasswordEncoder = encoder;
    }

    public void encodeNewPasswordForUser(Utilisateur utilisateur, String password) {
        String salt = new BigInteger(40, new SecureRandom()).toString(32);
        utilisateur.setSalt(salt);
        utilisateur.setPassword(messageDigestPasswordEncoder.encodePassword(password, salt));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.authentication.dao.
     * AbstractUserDetailsAuthenticationProvider
     * #additionalAuthenticationChecks(org
     * .springframework.security.core.userdetails.UserDetails,
     * org.springframework
     * .security.authentication.UsernamePasswordAuthenticationToken)
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails arg0, UsernamePasswordAuthenticationToken arg1) throws AuthenticationException {
        return;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.authentication.dao.
     * AbstractUserDetailsAuthenticationProvider#retrieveUser(java.lang.String,
     * org
     * .springframework.security.authentication.UsernamePasswordAuthenticationToken
     * )
     */
    @Override
    @Transactional
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        logger.log(Level.DEBUG, "Inside retrieveUser");

        String password = (String) authentication.getCredentials();
        if (!StringUtils.hasText(password)) {
            throw new BadCredentialsException("Please enter password");
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        try {
            logger.debug("Looking for user with identifiant : " + username);
            Utilisateur targetUtilisateur = utilisateurService.findUtilisateursWithoutCase(username);

            logger.debug("Found user with identifiant : " + username + " with id : " + targetUtilisateur.getId());

            // Verify user is active
            if (!targetUtilisateur.isFullyActive()) {
                throw new DisabledException("User " + username + " is not active in the database.");
            }

            // Exception si les credentials fournis ne sont pas valides
            validateCredentials(targetUtilisateur, password);

            logger.debug("Looking for organisme for user : " + targetUtilisateur.getEmail());
            Organisme organisme = targetUtilisateur.getOrganisme();

            logger.debug("Found organisme de type : " + organisme.getTypeOrganisme().getCode() + " pour l'utilisateur " + targetUtilisateur.getEmail());
            ProfilUtilisateur profilUtilisateur = targetUtilisateur.getProfilUtilisateur();
            if (profilUtilisateur != null && organisme.getProfilOrganisme() != null) {
                try {
                    authorities.addAll(provider.getProfileAccessRights(profilUtilisateur, organisme.getProfilOrganisme()));
                } catch (BusinessException e) {
                    logger.error("Impossible de charger le profil " + profilUtilisateur.toString() + " pour l'utilisateur : " + targetUtilisateur.getEmail(), e);
                    throw new AuthenticationServiceException("Droit de profils incorrects. Configuration de l'utilisateur incorrecte. Contactez votre administrateur");
                }
            } else {
                throw new AuthenticationServiceException("Profil non renseigné. Configuration de l'utilisateur incorrecte. Contactez votre administrateur");
            }

        } catch (EmptyResultDataAccessException e) {
            throw new BadCredentialsException("Invalid user");
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("Invalid user");
        } catch (NonUniqueResultException e) {
            throw new AuthenticationServiceException("Non-unique user, contact administrator");
        }

        return new org.springframework.security.core.userdetails.User(username, password, true, // enabled
                true, // account not expired
                true, // credentials not expired
                true, // account not locked
                authorities);
    }

    /**
     * Dans tous les cas, on récupère l'utilisateur de la base en amont.
     * On essaie de l'identifier à partir de l'annuaire ou de Remocra.
     *
     * @param dbRetrievedUser
     * @param password
     * @throws BadCredentialsException
     */
    void validateCredentials(Utilisateur dbRetrievedUser, String password) throws BadCredentialsException {

        // Etape 1 : authentification LDAP
        if (ldapAuthentication(dbRetrievedUser.getIdentifiant(), password)) {
            // OK (LDAP)
            return;
        }

        // Etape 2 : authentification à partir de la base
        String expectedPassEncoded = dbRetrievedUser.getPassword();
        if (!StringUtils.hasText(expectedPassEncoded)) {
            throw new BadCredentialsException("No password for " + dbRetrievedUser.getIdentifiant() + " set in database, contact administrator");
        }

        if (!messageDigestPasswordEncoder.encodePassword(password, dbRetrievedUser.getSalt()).equals(expectedPassEncoded)) {
            throw new BadCredentialsException("Invalid Password");
        }

        // OK (base de données)
    }

    boolean ldapAuthentication(String username, String password) {
        ParamConfService.LdapMethod method = paramConfService.getLdapMethod();
        switch (method) {
            case SIMPLE:
                return ldapAuthenticationDistinguishedName(username, password);
            case SEARCHUSER:
                return LdapAuthenticationSearchUser(username, password);
            default:
                return false;
        }
    }

    boolean ldapAuthenticationDistinguishedName(String username, String password) {
        Hashtable<String, String> ldapEnv = paramConfService.getLdapEnvironmentSimple();
        if (ldapEnv != null) {
            ldapEnv.put(Context.SECURITY_PRINCIPAL, username);
            ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
            DirContext ctx = null;
            try {
                ctx = new InitialDirContext(ldapEnv);
                // OK
                return true;
            } catch (NamingException e) {
                logger.info("Erreur authent. LDAP (méthode Simple) -> authent. Base", e);
            } finally {
                if (ctx!=null) {
                    try {
                        ctx.close();
                    } catch (Exception e) { /* Rien */ }
                }
            }
        }
        // KO
        return false;
    }

    boolean LdapAuthenticationSearchUser(String username, String password) {
        Hashtable<String, String> ldapEnv = paramConfService.getLdapEnvironmentSearchUser();
        if (ldapEnv != null) {
            DirContext ctx;
            try {
                ctx = new InitialDirContext(ldapEnv);
            } catch (NamingException e) {
                logger.info("Impossible de requêter l'annuaire LDAP");
                return false;
            }

            NamingEnumeration<SearchResult> results = null;

            try {
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                controls.setCountLimit(1);
                controls.setTimeLimit(5000);

                String ldapUserBaseName = paramConfService.getLdapUserBaseName();
                String ldapUserFilter = paramConfService.getLdapUserFilter();
                ldapUserFilter = ldapUserFilter.replace("[USERNAME]", username);
                results = ctx.search(ldapUserBaseName, ldapUserFilter, controls);

                if (!results.hasMore()) {
                    // Utilisateur non trouvé
                    return false;
                }
                SearchResult result = (SearchResult) results.next();
                String dn = (String) result.getAttributes().get("distinguishedName").get();

                return ldapAuthenticationDistinguishedName(dn, password);

            } catch (javax.naming.AuthenticationException e) {
                return false;
            } catch (NameNotFoundException e) {
                return false;
            } catch (SizeLimitExceededException e) {
                throw new RuntimeException("Trop de résultats retournés par une requête LDAP", e);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } finally {
                if (results != null) {
                    try {
                        results.close();
                    } catch (Exception e) { /* Rien */ }
                }
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (Exception e) { /* Rien */ }
                }
            }
        }
        return false;
    }

}
