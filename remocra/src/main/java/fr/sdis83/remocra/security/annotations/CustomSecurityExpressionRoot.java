package fr.sdis83.remocra.security.annotations;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.security.AuthoritiesUtil;

/**
 * Surcharge ou rajoute de nouvelle methodes pouvant être utilisée dans les
 * annotations de sécurité Pre et Post.
 * 
 * @author bpa
 * 
 */
public class CustomSecurityExpressionRoot extends SecurityExpressionRoot {

    private AuthoritiesUtil authUtils;

    // parent constructor
    public CustomSecurityExpressionRoot(Authentication a, AuthoritiesUtil authUtils, CustomPermissionEvaluator permEval) {
        super(a);
        this.authUtils = authUtils;
        this.setPermissionEvaluator(permEval);
    }

    /**
     * Authorisation pour un droit
     * 
     * @param typeDroit
     * @return boolean
     */
    public boolean hasRight(TypeDroitEnum typeDroit) {
        return authUtils.hasRight(typeDroit);
    }

}
