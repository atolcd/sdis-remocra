package fr.sdis83.remocra.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.security.AccessRight.Permission;
import fr.sdis83.remocra.service.AuthService;

@Configuration
public class AuthoritiesUtil {

    private static final Logger logger = Logger.getLogger(AuthoritiesUtil.class);

    @Bean
    public AuthoritiesUtil authoritiesUtil() {
        return new AuthoritiesUtil();
    }

    public boolean hasRight(TypeDroitEnum typeDroitEnum, Permission perm) {
        Collection<? extends GrantedAuthority> authorities = AuthService.getCurrentAuth().getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {

            if (!(grantedAuthority instanceof AccessRight)) {
                continue;
            }

            AccessRight ar = (AccessRight) grantedAuthority;
            if (ar.getKey().equals(typeDroitEnum)) {
                logger.debug(" Found right for key " + typeDroitEnum.getValue() + " with perms : " + ar.permissionsToString());
                if (ar.hasPermission(perm)) {
                    logger.debug(" Found right : " + perm + " for key : " + typeDroitEnum.getValue());
                    return true;
                }
            }
        }
        logger.debug(" No right for key : " + typeDroitEnum.getValue());
        return false;
    }

    public List<AccessRight> getCurrentRights() {
        List<AccessRight> result = new ArrayList<AccessRight>();
        Collection<? extends GrantedAuthority> authorities = AuthService.getCurrentAuth().getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {

            if (!(grantedAuthority instanceof AccessRight)) {
                continue;
            }
            result.add((AccessRight) grantedAuthority);
        }
        return result;
    }
}
