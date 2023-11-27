package fr.sdis83.remocra.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.GestionnaireSite;
import fr.sdis83.remocra.repository.GestionnaireSiteRepository;
import fr.sdis83.remocra.usecase.gestionnaireSite.DeleteGestionnaireSiteUseCase;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/gestionnairesite")
@Controller
public class GestionnaireSiteController {
  @Autowired GestionnaireSiteRepository gestionnaireSiteRepository;
  @Autowired DeleteGestionnaireSiteUseCase deleteGestionnaireSiteUseCase;

  private final Logger logger = Logger.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  /**
   * Retourne un GestionnaireSite par son Id
   *
   * @param idGestionnaireSite
   * @return un objet GestionnaireSite
   */
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_L') or hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<String> listJson(@PathVariable("id") final Long idGestionnaireSite) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              gestionnaireSiteRepository.getGestionnaireSiteById(idGestionnaireSite)),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Récupère tous les gestionnaire_site actif d'un gestionnaire
   *
   * @param idGestionnaire
   * @return une liste d'id et de nom de gesitonnaire_site sous forme d'objet gesitonnaire_site
   */
  @RequestMapping(
      value = "/getComboSiteByGestionnaireId/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<String> getComboSiteByGestionnaireId(
      @PathVariable("id") final Long idGestionnaire) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              gestionnaireSiteRepository.getComboSiteByGestionnaireId(idGestionnaire)),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/updateGestionnaireSite/{id}",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<java.lang.String> updateGestionnaireSite(
      @PathVariable("id") Long idGestionnaireSite, HttpServletRequest request) {
    try {
      GestionnaireSite objGestionnaireSite =
          objectMapper.readValue(
              request.getParameter("gestionnaireSite"), new TypeReference<GestionnaireSite>() {});
      gestionnaireSiteRepository.updateGestionnaireSite(idGestionnaireSite, objGestionnaireSite);
      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/deleteGestionnaireSite/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<String> deleteGestionnaireSite(
      @PathVariable("id") Long idGestionnaireSite) {
    try {
      deleteGestionnaireSiteUseCase.deleteGestionnaireSite(idGestionnaireSite);
      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retourne une liste de GestionnaireSiteInfos
   *
   * @return une liste d'objet GestionnaireSiteInfos
   */
  @RequestMapping(
      value = "/manageGestionnaireSite",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_L')")
  public ResponseEntity<String> fetchGestionnaireSiteData() {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              gestionnaireSiteRepository.getListGestionnaireSiteInfos()),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retourne la liste des hydrants lié au gestionnaireSite donnée
   *
   * @param idGestionnaireSite
   * @return une liste de String numéro hydrant
   */
  @RequestMapping(
      value = "/gethydrant/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<String> getHydrantByGestionnaireSiteId(
      @PathVariable("id") Long idGestionnaireSite) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              gestionnaireSiteRepository.getHydrantWithIdGestionnaireSite(idGestionnaireSite)),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Récupère la liste des contacts liés à un gestionnaire_site
   *
   * @param idGestionnaireSite
   * @return une liste de String de civilités, noms, prenoms, fonctions de contacts
   */
  @RequestMapping(
      value = "/getcontact/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('GESTIONNAIRE_E')")
  public ResponseEntity<String> getContactByGestionnaireSiteId(
      @PathVariable("id") Long idGestionnaireSite) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              gestionnaireSiteRepository.getContactWithIdGestionnaireSite(idGestionnaireSite)),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
