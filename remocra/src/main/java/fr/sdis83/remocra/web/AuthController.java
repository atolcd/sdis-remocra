package fr.sdis83.remocra.web;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Utilisateur;
import fr.sdis83.remocra.repository.UtilisateurRepository;
import fr.sdis83.remocra.service.AuthService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.net.URISyntaxException;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Gère les connexions / déconnexions des utilisateurs.
 *
 * @author cva
 */
@RequestMapping("/auth")
@Controller
public class AuthController {

  @Autowired private AuthService authService;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired public UtilisateurRepository utilisateurRepository;

  public static final String DUMMY_RESPONSE_XML_OK =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><message>Opération réalisée avec succès</message></success>";

  /**
   * Connexion de l'utilisateur.
   *
   * @param username
   * @param password
   * @return 200 en cas de succès, 401 sinon
   * @throws URISyntaxException
   * @throws AuthenticationException
   */
  @RequestMapping(
      value = "/login",
      method = RequestMethod.POST,
      produces = "application/json;charset=utf-8")
  public String login(
      @RequestParam String username,
      @RequestParam String password,
      HttpServletRequest request,
      HttpSession session) {

    // Si nouvel utilisateur alors on réinitialise la session.
    // On considère que le bon identifiant est celui qui vient de la base et non celui saisi par
    // l'utilisateur
    Utilisateur utilisateur = utilisateurRepository.findUtilisateurWithoutCase(username);
    if (utilisateur == null) {
      return "redirect:/auth/login/failure";
    }
    if (session != null
        && !(AuthService.isUserAuthenticated()
            && AuthService.getCurrentUserIdentifiant().equals(utilisateur.getIdentifiant()))) {
      session.invalidate();
    }
    // Create a new session for the user.
    session = request.getSession(true);
    session.setMaxInactiveInterval(-1);

    String autResult = authService.authUser(utilisateur.getIdentifiant(), password);
    // On utilise le redirect sinon le header Set-Cookie avec le nouveau
    // JSESSIONID n'est pas renseigné...
    if (autResult == null) {
      return "redirect:/auth/login/failure";
    } else {
      utilisateurRepository.postDerniereConnexion(utilisateur.getId());
      return "redirect:/utilisateurs/current";
    }
  }

  /**
   * Connexion de l'utilisateur.
   *
   * @param username
   * @param password
   * @return 200 en cas de succès, 401 sinon
   * @throws URISyntaxException
   * @throws AuthenticationException
   */
  @RequestMapping(
      value = "/login/xml",
      method = RequestMethod.POST,
      produces = "application/xml;charset=utf-8")
  public String loginXML(
      @RequestParam String username,
      @RequestParam String password,
      HttpServletRequest request,
      HttpSession session) {

    // Si nouvel utilisateur alors on réinitialise la session.
    if (session != null
        && !(AuthService.isUserAuthenticated()
            && AuthService.getCurrentUserIdentifiant().equals(username))) {
      session.invalidate();
    }
    // Create a new session for the user.
    session = request.getSession(true);
    session.setMaxInactiveInterval(-1);

    String autResult = authService.authUser(username, password);
    // On utilise le redirect sinon le header Set-Cookie avec le nouveau
    // JSESSIONID n'est pas renseigné...
    if (autResult == null) {
      return "redirect:/auth/login/failure";
    } else {
      return "redirect:/utilisateurs/current/xml";
    }
  }

  @RequestMapping(value = "/login/failure", produces = "application/json;charset=utf-8")
  public ResponseEntity<String> loginFailure() {
    return new SuccessErrorExtSerializer(
            false, "Impossible de connecter l'utilisateur", HttpStatus.UNAUTHORIZED)
        .serialize();
  }

  /**
   * Déconnexion de l'utilisateur.
   *
   * @return
   */
  @RequestMapping(
      value = "/logout",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json;charset=utf-8")
  public ResponseEntity<String> logout() {
    AuthService.logoutUser();
    return new SuccessErrorExtSerializer(true, "Utilisateur déconnecté").serialize();
  }

  @RequestMapping(
      value = "/logout/xml",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml;charset=utf-8")
  public ResponseEntity<String> logoutXML() {
    AuthService.logoutUser();
    return new ResponseEntity<String>(DUMMY_RESPONSE_XML_OK, HttpStatus.OK);
  }
}
