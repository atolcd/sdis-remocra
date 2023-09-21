package fr.sdis83.remocra.security.annotations;

import fr.sdis83.remocra.security.AuthoritiesUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

/**
 * Permet de surcharger le SecurityExpressionRoot
 *
 * @author bpa
 */
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler
    implements ApplicationContextAware {

  @Autowired private AuthoritiesUtil authUtils;

  @Autowired private CustomPermissionEvaluator customPermissionEvaluator;

  public void setAuthUtils(AuthoritiesUtil authUtils) {
    this.authUtils = authUtils;
  }

  // parent constructor
  public CustomMethodSecurityExpressionHandler() {
    super();
  }

  /**
   * Custom override to use {@link CustomSecurityExpressionRoot}
   *
   * <p>Uses a {@link MethodSecurityEvaluationContext} as the <tt>EvaluationContext</tt>
   * implementation and configures it with a {@link MethodSecurityExpressionRoot} instance as the
   * expression root object.
   */
  @Override
  public SecurityExpressionRoot createSecurityExpressionRoot(
      Authentication auth, MethodInvocation mi) {
    CustomSecurityExpressionRoot customSecurityExpressionRoot =
        (CustomSecurityExpressionRoot)
            new CustomSecurityExpressionRoot(auth, authUtils, customPermissionEvaluator);
    return customSecurityExpressionRoot;
  }
}
