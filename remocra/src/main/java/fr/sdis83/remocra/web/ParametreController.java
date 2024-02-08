package fr.sdis83.remocra.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.datasource.CodeLibelleOrdreData;
import fr.sdis83.remocra.enums.PeiCaracteristique;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.affichageIndispo.AffichageIndispoUseCase;
import fr.sdis83.remocra.usecase.parametre.agents.AgentsUseCase;
import fr.sdis83.remocra.usecase.parametre.caracteristiques.CaracteristiqueUseCase;
import fr.sdis83.remocra.usecase.parametre.passwordAdmin.PasswordAdminUseCase;
import fr.sdis83.remocra.usecase.parametre.validiteToken.ValiditeTokenUseCase;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/parametre")
@Controller
public class ParametreController {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired CaracteristiqueUseCase caracteristiquesUseCase;
  @Autowired AgentsUseCase agentsUseCase;
  @Autowired AffichageIndispoUseCase affichageIndispoUseCase;
  @Autowired PasswordAdminUseCase passwordAdminUseCase;
  @Autowired ValiditeTokenUseCase validiteTokenUseCase;

  @Autowired ParametreDataProvider parametreDataProvider;

  @RequestMapping(
      value = "/caracteristiques/nonChoisie/{type}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getCaracteristiquesPossible(
      @PathVariable("type") PeiCaracteristique.TypeCaracteristique type) {
    try {

      return new ResponseEntity<>(
          caracteristiquesUseCase.getCaracteristiquesNonChoisiesByType(type), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/caracteristiques/choisie/{type}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getCaracteristiquesSelectByType(
      @PathVariable("type") PeiCaracteristique.TypeCaracteristique type) {
    try {

      return new ResponseEntity<>(
          caracteristiquesUseCase.getCaracteristiquesChoisiesByType(type), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/agents/selected/",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getParametreAgentsSelected() {
    try {

      return new ResponseEntity<>(agentsUseCase.getTypeAgentsSelected(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/agents/selectable/",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getAllParametreAgents() {
    try {

      return new ResponseEntity<>(agentsUseCase.getTypeAgentsSelectable(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/agents/update/",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updateAgent(HttpServletRequest request) {

    try {
      // On récupére le code de a insérer en base
      String agentParam =
          objectMapper.readValue(request.getParameter("agent"), new TypeReference<String>() {});

      caracteristiquesUseCase.updateAgentParam(agentParam);

      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/caracteristiques/update/",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updateCaracteristiquesSelectByType(HttpServletRequest request) {

    try {
      // On récupére les objet "CodeLibelleDataOrdre" sélectionné pour les pibi
      List<CodeLibelleOrdreData> pibiValeur =
          objectMapper.readValue(
              request.getParameter("pibi"), new TypeReference<List<CodeLibelleOrdreData>>() {});
      // On récupére les objet "CodeLibelleDataOrdre" sélectionné pour les pena
      List<CodeLibelleOrdreData> penaValeur =
          objectMapper.readValue(
              request.getParameter("pena"), new TypeReference<List<CodeLibelleOrdreData>>() {});

      caracteristiquesUseCase.updateCaracteristiquesChoisieByType(pibiValeur, penaValeur);

      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/srid",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  public ResponseEntity<String> getSrid() {
    return new ResponseEntity<>(
        parametreDataProvider.get().getValeurString(GlobalConstants.CLE_SRID), HttpStatus.OK);
  }

  @RequestMapping(
      value = "/affichageIndispo",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  public ResponseEntity<String> getAffichageIndispo() {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(affichageIndispoUseCase.getAffichageIndispo()),
          HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/affichageIndispo/update/",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updateAffichageIndispo(HttpServletRequest request) {

    try {
      Boolean affichageIndispoParam =
          objectMapper.readValue(request.getParameter("affichageIndispo"), Boolean.class);

      affichageIndispoUseCase.updateAffichageIndispoParam(affichageIndispoParam);

      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/passwordAdmin",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  public ResponseEntity<String> getPasswordAdmin() {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(passwordAdminUseCase.getPasswordAdmin()), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/passwordAdmin/update/",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updatePasswordAdmin(HttpServletRequest request) {

    try {
      // On récupére le code de a insérer en base
      String passwordAdminParam =
          objectMapper.readValue(request.getParameter("passwordAdmin"), String.class);

      passwordAdminUseCase.updatePasswordAdminParam(passwordAdminParam);

      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/validiteToken",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  public ResponseEntity<String> getValiditeToken() {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(validiteTokenUseCase.getValiditeToken()), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @RequestMapping(
      value = "/validiteToken/update/",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updateValiditeToken(HttpServletRequest request) {

    try {
      // On récupére le code à insérer en base
      Integer validiteTokenParam =
          objectMapper.readValue(request.getParameter("validiteToken"), Integer.class);

      validiteTokenUseCase.updateValiditeTokenParam(validiteTokenParam);

      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
