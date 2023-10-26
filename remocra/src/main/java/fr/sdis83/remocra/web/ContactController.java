package fr.sdis83.remocra.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.repository.ContactRolesRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/contact")
@Controller
public class ContactController {

  @Autowired ContactRepository contactRepository;
  @Autowired ContactRolesRepository contactRolesRepository;

  private final Logger logger = Logger.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(
      value = "/{idappartenance}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  public ResponseEntity<String> listJson(
      @PathVariable("idappartenance") final String idappartenance,
      final @RequestParam(value = "appartenance", required = false) String appartenance) {
    return new AbstractExtListSerializer<fr.sdis83.remocra.domain.remocra.Contact>(
        "fr.sdis83.remocra.domain.remocra.Contact retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer()
            .include("data.roles")
            .exclude("data.roles.thematique", "data.roles.actif", "data.roles.version")
            .exclude("*.class");
      }

      @Override
      protected List<fr.sdis83.remocra.domain.remocra.Contact> getRecords() {
        return contactRepository.findAllContactById(appartenance, idappartenance);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/contactInfos/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getContact(@PathVariable("id") Long id) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(contactRepository.getContact(id)), HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/contactRoles/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> getContactRolesById(@PathVariable("id") Long id) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(contactRepository.getContactRolesById(id)),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/updateContact/{id}",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> updateContact(
      HttpServletRequest request, @PathVariable("id") final Long idContact) {
    try {
      try {
        Contact objContact =
            objectMapper.readValue(
                request.getParameter("contact"), new TypeReference<Contact>() {});
        contactRepository.updateContact(idContact, objContact);
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      try {
        contactRolesRepository.deleteContactRolesById(idContact);
        if (!request.getParameter("role").equals(null) && !request.getParameter("role").isEmpty()) {
          List<String> listRolesS =
              new ArrayList<>(Arrays.asList(request.getParameter("role").split(",")));
          if (!listRolesS.isEmpty()) {
            for (String s : listRolesS)
              contactRolesRepository.createContactRoles(idContact, Long.valueOf(s));
          }
        }
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/createContact",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> createContact(HttpServletRequest request) {
    Long idContact_ = null;
    try {
      try {
        Contact objContact =
            objectMapper.readValue(
                request.getParameter("contact"), new TypeReference<Contact>() {});
        idContact_ = contactRepository.createContact(objContact);
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      try {
        if (!request.getParameter("role").equals(null) && !request.getParameter("role").isEmpty()) {
          List<String> listRolesS =
              new ArrayList<>(Arrays.asList(request.getParameter("role").split(",")));
          if (!listRolesS.isEmpty()) {
            for (String s : listRolesS)
              contactRolesRepository.createContactRoles(idContact_, Long.valueOf(s));
          }
        }
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "deleteContact/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=Application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> deleteContact(@PathVariable("id") final Long idContact) {
    try {
      try {
        contactRolesRepository.deleteContactRolesById(idContact);
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      try {
        contactRepository.deleteContact(idContact);
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>("Succes", HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
