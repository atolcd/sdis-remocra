package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalieNature;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.TypeHydrantAnomalieNatureService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/typehydrantanomalienatures")
@Controller
public class TypeHydrantAnomalieNatureController {

  @Autowired private TypeHydrantAnomalieNatureService service;

  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
    try {
      final TypeHydrantAnomalieNature attached = service.create(json, null);
      return new AbstractExtObjectSerializer<TypeHydrantAnomalieNature>(
          "TypeHydrantAnomalieNature created") {
        @Override
        protected TypeHydrantAnomalieNature getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> update(
      final @PathVariable Long id, final @RequestBody String json) {
    try {
      final TypeHydrantAnomalieNature attached = service.update(id, json, null);
      if (attached != null) {
        return new AbstractExtObjectSerializer<TypeHydrantAnomalieNature>(
            "TypeHydrantAnomalieNature updated.") {
          @Override
          protected TypeHydrantAnomalieNature getRecord() throws BusinessException {
            return attached;
          }
        }.serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
    return new SuccessErrorExtSerializer(false, "Hydrant Pena inexistant", HttpStatus.NOT_FOUND)
        .serialize();
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
    try {
      service.delete(id);
      return new SuccessErrorExtSerializer(true, "TypeHydrantAnomalieNature supprimé").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
