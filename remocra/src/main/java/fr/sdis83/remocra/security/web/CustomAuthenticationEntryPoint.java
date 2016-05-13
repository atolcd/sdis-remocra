package fr.sdis83.remocra.security.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import fr.sdis83.remocra.util.ExceptionUtils;

/**
 * Gère les exceptions lors de l'authentification 401 si en AJAX ou redirect si
 * HTML
 * 
 * @author cva
 * 
 */
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final Logger logger = Logger.getLogger(getClass());

    public CustomAuthenticationEntryPoint() {
        super("/login");
    }

    public CustomAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    /**
     * Permet de prendre en compte les 401/AuthenticationException pour la
     * requete AJAX d'Authentification
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String xrequestedWith = ((HttpServletRequest) request).getHeader("x-requested-with");
        boolean isAjax = xrequestedWith != null && "XMLHttpRequest".equals(xrequestedWith);

        if (isAjax && authException != null) {

            logger.debug("Intercepting Authentication Exception to prevent redirect to Login : ", authException);

            // Requête Ajax => Gestion par statuts HTTP 401
            AuthenticationException e = ExceptionUtils.getNestedExceptionWithClass(authException, AuthenticationException.class);
            if (e != null) {

                // JsonObjectResponse jsonData = new JsonObjectResponse();
                // jsonData.setMessage(e.getMessage() != null ? e.getMessage() :
                // "Erreur inconnue");
                // jsonData.setSuccess(false);
                // jsonData.setTotal(0L);
                //
                // response.setHeader("Content-Type",
                // "application/json;charset=utf-8");
                // response.getWriter().write(new
                // JSONSerializer().exclude("*.class").serialize(jsonData));
                //
                // // 401 : authentification nécessaire
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        // Comportement par défaut
        super.commence(request, response, authException);
    }
}
