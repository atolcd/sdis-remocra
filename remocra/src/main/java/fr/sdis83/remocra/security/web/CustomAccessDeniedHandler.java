package fr.sdis83.remocra.security.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.json.JsonObjectResponse;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

/**
 * Gère les Exception d'accès refusé (soit par intercept-url soit par annotations Pre ou Post. Si
 * requête Ajax, 403 sans redirection vers la page d'erreur. Si HTML, redirect vers page d'erreur.
 *
 * @author cva
 */
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    String xrequestedWith = ((HttpServletRequest) request).getHeader("x-requested-with");
    boolean isAjax = xrequestedWith != null && "XMLHttpRequest".equals(xrequestedWith);

    if (isAjax && accessDeniedException != null) {

      logger.debug(
          "Intercepting AccessDeniedException to prevent redirect to Error Page.",
          accessDeniedException);

      JsonObjectResponse jsonData = new JsonObjectResponse();
      jsonData.setMessage(
          accessDeniedException.getMessage() != null
              ? accessDeniedException.getMessage()
              : "Erreur inconnue");
      jsonData.setSuccess(false);
      jsonData.setTotal(0L);

      response.setHeader(
          "Content-Type",
          request.getContentType() != null
                  && request.getContentType().contains("multipart/form-data")
              ? SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE
              : AbstractExtObjectSerializer.DEFAULT_CONTENT_TYPE);
      response.getWriter().write(new JSONSerializer().exclude("*.class").serialize(jsonData));

      // 401 : authentification nécessaire
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    // Comportement par défaut
    super.handle(request, response, accessDeniedException);
  }
}
