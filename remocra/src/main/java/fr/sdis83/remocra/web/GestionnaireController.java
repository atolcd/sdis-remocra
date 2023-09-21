package fr.sdis83.remocra.web;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.domain.remocra.Gestionnaire;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.service.GestionnaireService;
import fr.sdis83.remocra.usecase.contacts.ManageContactsUseCase;
import fr.sdis83.remocra.usecase.gestionnaire.DeleteGestionnaireUseCase;
import fr.sdis83.remocra.usecase.gestionnaire.GestionnaireInfos;
import fr.sdis83.remocra.usecase.gestionnaire.ManageGestionnaireUseCase;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/gestionnaire")
@Controller
public class GestionnaireController {

  @Autowired GestionnaireService service;

  @Autowired ContactRepository contactRepository;

  @Autowired DeleteGestionnaireUseCase deleteGestionnaireUseCase;

  @Autowired ManageGestionnaireUseCase manageGestionnaireUseCase;
  @Autowired ManageContactsUseCase manageContactsUseCase;
  @Autowired GestionnaireRepository gestionnaireRepository;

  private final Logger logger = Logger.getLogger(getClass());

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<String> listJson() {
    return new AbstractExtListSerializer<Gestionnaire>(
        "fr.sdis83.remocra.domain.remocra.Gestionnaire retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer().exclude("*.class");
      }

      @Override
      protected List<Gestionnaire> getRecords() {
        return Gestionnaire.findAllGestionnaires();
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<String> listJson(@PathVariable("id") final Long id) {
    return new AbstractExtObjectSerializer<Gestionnaire>(
        "fr.sdis83.remocra.domain.remocra.Gestionnaire retrieved.") {

      @Override
      protected Gestionnaire getRecord() throws BusinessException {
        return Gestionnaire.findGestionnaire(id);
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer().exclude("*.class");
      }
    }.serialize();
  }

  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
  public ResponseEntity<java.lang.String> create(HttpServletRequest request) {
    try {
      String gestionnaire = request.getParameter("gestionnaire");
      String contactsJson = request.getParameter("contacts");
      String appartenance = request.getParameter("appartenance");

      final Gestionnaire attached = service.create(gestionnaire, null);

      contactRepository.createContactsFromJson(contactsJson, appartenance, attached.getId());

      return new AbstractExtObjectSerializer<Gestionnaire>("Gestionnaire created") {
        @Override
        protected Gestionnaire getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
  public ResponseEntity<java.lang.String> update(
      HttpServletRequest request, @PathVariable("id") final Long id) {
    try {
      String gestionnaire = request.getParameter("gestionnaire");
      String contactsJson = request.getParameter("contacts");
      String appartenance = request.getParameter("appartenance");

      final Gestionnaire attached = service.update(id, gestionnaire, null);
      contactRepository.updateContactsFromJson(contactsJson, appartenance, id);

      return new AbstractExtObjectSerializer<Gestionnaire>("Gestionnaire created") {
        @Override
        protected Gestionnaire getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "/delete/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
  public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
    try {
      deleteGestionnaireUseCase.execute(id);
      return new SuccessErrorExtSerializer(true, "Gestionnaire supprimé").serialize();
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "/manageGestionnaire",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> fetchGestionnaireData() {
    return new AbstractExtListSerializer<GestionnaireInfos>("Get Gestionnaires Informations") {
      @Override
      protected List<GestionnaireInfos> getRecords() throws BusinessException {
        return manageGestionnaireUseCase.fetchGestionnaireData();
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/listeContact/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> fetchContactsDataByGestionnaireId(@PathVariable("id") Long id) {
    return new AbstractExtListSerializer<Contact>("Get Gestionnaire's contacts") {
      @Override
      protected List<Contact> getRecords() throws BusinessException {
        return manageContactsUseCase.fetchContactsDataByGestionnaireId(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/listeGestionnaireCodes",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getGestionnaireCodes() {
    return new AbstractExtListSerializer<String>("Get Gestionnaires' codes") {
      @Override
      protected List<String> getRecords() throws BusinessException {
        return gestionnaireRepository.getGestionnaireCodes();
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/createGestionnaire",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> createGestionnaire(HttpServletRequest request) {
    try {
      String gestionnaire = request.getParameter("gestionnaire");
      fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire objGestionnaire =
          new JSONDeserializer<fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire>()
              .use(null, fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire.class)
              .deserialize(gestionnaire);
      gestionnaireRepository.createGestionnaire(objGestionnaire);
      return new SuccessErrorExtSerializer(true, "").serialize();
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors de la création d'un gestionnaire'")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/updateGestionnaire/{id}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> updateGestionnaire(
      @PathVariable("id") Long id, HttpServletRequest request) {
    try {
      String gestionnaire = request.getParameter("gestionnaire");
      fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire objGestionnaire =
          new JSONDeserializer<fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire>()
              .use(null, fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire.class)
              .deserialize(gestionnaire);
      gestionnaireRepository.updateGestionnaire(id, objGestionnaire);
      return new SuccessErrorExtSerializer(true, "").serialize();
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors de la mise à jour d'un gestionnaire")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/getHydrant/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> getHydrantByGestionnaireId(@PathVariable("id") Long id) {
    return new AbstractExtListSerializer<String>("Get hydrant num by Gestionnaire Id") {
      @Override
      protected List<String> getRecords() throws BusinessException {
        return gestionnaireRepository.getHydrantWithIdGestionnaire(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/deleteGestionnaire/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> deleteGestionnaire(@PathVariable("id") Long id) {
    try {
      deleteGestionnaireUseCase.deleteGestionnaire(id);
      return new SuccessErrorExtSerializer(true, "Gestionnaire intégralement supprimé").serialize();
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
