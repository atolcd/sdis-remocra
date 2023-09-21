package fr.sdis83.remocra.web;

import fr.sdis83.remocra.repository.AccesAPIOrganismeRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/accesAPIOrganisme")
@Controller
public class AccesAPIOrganismeController {

  @Autowired AccesAPIOrganismeRepository accesAPIOrganismeRepository;

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @Transactional
  public ResponseEntity<String> newDdeApi(@PathVariable("id") Long idOrganisme) {
    try {
      accesAPIOrganismeRepository.newDdeApi(idOrganisme);
      return new SuccessErrorExtSerializer(
              true,
              "La demande d'accès à l'API a bien été prise en compte. Un mail va être envoyé à l'adresse renseignée.")
          .serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la demande d'accès à l'API")
          .serialize();
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<String> getCodeValidity(@PathVariable("id") String codeAPIOrganisme) {
    return new AbstractExtObjectSerializer<Boolean>(
        "fr.sdis83.remocra.domain.remocra.AccesAPIOrganismeController retrieved.") {

      @Override
      protected Boolean getRecord() {
        return accesAPIOrganismeRepository.getCodeValidity(codeAPIOrganisme);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "password/{code}",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @Transactional
  public ResponseEntity<String> setPassword(
      @PathVariable("code") String code, @RequestHeader("pwd") String password) {
    try {
      accesAPIOrganismeRepository.setPasswordOrganisme(code, password);
      return new SuccessErrorExtSerializer(true, "Le mot de passe a été enregistré avec succès.")
          .serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
