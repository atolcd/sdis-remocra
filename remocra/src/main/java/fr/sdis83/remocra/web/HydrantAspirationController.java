package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.HydrantAspiration;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantAspirationService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/hydrantaspiration")
@Controller
public class HydrantAspirationController {

  @Autowired private HydrantAspirationService hydrantAspirationService;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  public ResponseEntity<java.lang.String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);

    if (query != null) {
      itemFilterList.add(new ItemFilter("numero", query));
    }

    return new AbstractExtListSerializer<HydrantAspiration>("HydrantAspiration retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @Override
      protected List<HydrantAspiration> getRecords() {
        return hydrantAspirationService.find(start, limit, sortList, itemFilterList);
      }
    }.serialize();
  }

  /**
   * Mise à jour de plusieurs HydrantAspiration à la fois
   *
   * @param json Un tableau JSON des informations des HydrantAspiration
   */
  @RequestMapping(
      value = "/updatemany",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> update(final @RequestBody String json) {
    try {
      hydrantAspirationService.updateMany(json);
      return new SuccessErrorExtSerializer(true, "HydrantAspiration updated.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  /** Création d'une nouvelle aspiration */
  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
    try {
      final HydrantAspiration attached = hydrantAspirationService.create(json, null);
      return new AbstractExtObjectSerializer<HydrantAspiration>("HydrantAspiration created") {
        @Override
        protected HydrantAspiration getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  /**
   * Suppression d'une ou plusieurs aspirations
   *
   * @param json Un tableau d'entiers d'ID d'aspirations à supprimer
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.DELETE, headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> delete(final @RequestBody String json) {
    try {
      hydrantAspirationService.delete(json);
      return new SuccessErrorExtSerializer(true, "HydrantAspiration supprimé").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
