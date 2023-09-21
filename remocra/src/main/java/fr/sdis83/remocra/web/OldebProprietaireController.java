package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.OldebProprietaire;
import fr.sdis83.remocra.service.OldebProprietaireService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/proprietaire")
@Controller
public class OldebProprietaireController
    extends AbstractServiceableController<OldebProprietaireService, OldebProprietaire> {

  @Autowired private OldebProprietaireService service;

  @Override
  protected OldebProprietaireService getService() {
    return service;
  }

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> listJson(
          /*
           * final @RequestParam(
           * value = "page", required
           * = false) Integer page,
           */
          final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    // Ajout du filtre sur le nom
    if (query != null) {
      itemFilterList.add(new ItemFilter("nomProprietaire", query));
    }

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    // Ajout du tri par défaut par ordre croissant
    if (sortList.isEmpty()) {
      sortList.add(new ItemSorting("nomProprietaire", "ASC"));
    }

    return new AbstractExtListSerializer<OldebProprietaire>("OldebProprietaire retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {

        return serializer.include("total").include("message");
      }

      @Override
      protected List<OldebProprietaire> getRecords() {
        return service.find(start, limit, sortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return service.count(itemFilterList);
      }
    }.serialize();
  }
}
