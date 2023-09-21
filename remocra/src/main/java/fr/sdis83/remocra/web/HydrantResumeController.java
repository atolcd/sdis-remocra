package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.HydrantResume;
import fr.sdis83.remocra.service.HydrantResumeService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/hydrantResume")
@Controller
public class HydrantResumeController {

  @Autowired private HydrantResumeService hydrantResumeService;

  @Autowired private UtilisateurService utilisateurService;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

    return new AbstractExtListSerializer<HydrantResume>("HydrantResume retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer;
      }

      @Override
      protected List<HydrantResume> getRecords() {
        return hydrantResumeService.find(start, limit, itemSortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(hydrantResumeService.count(itemFilterList));
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/xml")
  public ResponseEntity<java.lang.String> resumeQuery(
      final @PathVariable Long id, final @RequestParam(value = "useDefault") Boolean useDefault) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json;charset=utf-8");
    Long idOrganismeUser = this.utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
    String xmlDoc = hydrantResumeService.resume(id, idOrganismeUser, useDefault);
    if (xmlDoc != null) {
      return new ResponseEntity<String>(xmlDoc.toString(), responseHeaders, HttpStatus.OK);
    } else {
      return new SuccessErrorExtSerializer(
              false,
              "remocra.hydrant_resume: Une erreur est survenue lors de l'exécution de la requête")
          .serialize();
    }
  }
}
