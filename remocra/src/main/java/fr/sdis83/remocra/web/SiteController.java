package fr.sdis83.remocra.web;

import fr.sdis83.remocra.service.SiteService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.Site;

import java.util.List;

@RequestMapping("/site")
@Controller
public class SiteController {

    @Autowired
    private SiteService siteService;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                               final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                               final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                               @RequestParam(value = "filter", required = false) String filters) {


    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

    return new AbstractExtListSerializer<Site>("Site retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer;
      }

      @Override
      protected List<Site> getRecords() {
        return siteService.find(start, limit, itemSortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return
           Long.valueOf(siteService.count(itemFilterList));
      }

    }.serialize();
  }

}