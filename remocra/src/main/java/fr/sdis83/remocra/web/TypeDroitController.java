package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.service.TypeDroitService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typedroits")
@Controller
public class TypeDroitController {

  @Autowired private TypeDroitService typeDroitService;

  @RequestMapping(headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    return new AbstractExtListSerializer<TypeDroit>("TypeDroit retrieved.") {

      @Override
      protected List<TypeDroit> getRecords() {
        return typeDroitService.find(start, limit, sortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return typeDroitService.count(itemFilterList);
      }
    }.serialize();
  }
}
