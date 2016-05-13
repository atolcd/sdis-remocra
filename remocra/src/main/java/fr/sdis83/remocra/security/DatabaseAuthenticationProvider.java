package fr.sdis83.remocra.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;

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

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;

/**
 * Permet d'utiliser la base de donnée pour la gestion des utilisateur de
 * l'application.
 * 
 * @author bpa
 * 
 */
public class DatabaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ProfileProvider provider;

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
            Utilisateur targetUtilisateur = Utilisateur.findUtilisateursByIdentifiant(username).getSingleResult();

            logger.debug("Found user with identifiant : " + username + " with id : " + targetUtilisateur.getId());

            // Verify user is active
            if (!targetUtilisateur.isFullyActive()) {
                throw new DisabledException("User " + username + " is not active in the database.");
            }

            // authenticate the person
            String expectedPassEncoded = targetUtilisateur.getPassword();
            if (!StringUtils.hasText(password)) {
                throw new BadCredentialsException("No password for " + username + " set in database, contact administrator");
            }

            if (!messageDigestPasswordEncoder.encodePassword(password, targetUtilisateur.getSalt()).equals(expectedPassEncoded)) {
                throw new BadCredentialsException("Invalid Password");
            }

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

}
