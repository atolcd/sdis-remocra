package fr.sdis83.remocra.security.annotations;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import fr.sdis83.remocra.security.AccessRight.Permission;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.UtilisateurService;

/**
 * Implementation utilisée pour les methodes hasPermission sur des objets.
 * 
 * @author bpa
 * 
 */
@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final Logger logger = Logger.getLogger(getClass());

    @Bean
    public CustomPermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator();
    }

    @Autowired
    private AuthoritiesUtil authUtils;

    @Autowired
    private UtilisateurService utilisateurService;

    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
        // return hasPermission(authentication, domainObject.getId(),
        // domainObject.getClass());
        return false;
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        Permission perm = Permission.valueOf((String) permission);
        logger.debug("Evaluating rights for " + targetType + " with id : " + targetId + " and permission : " + perm + " and user " + authentication.getName());

        if (targetId == null) {
            return false;
        }

        Long targetIdL = Long.parseLong(targetId.toString());

        // Cas particuliers

        return false;
    }
}
