package fr.sdis83.remocra.web;

import fr.sdis83.remocra.service.DebitSimultaneHydrantService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneHydrant;

import java.util.List;

@RequestMapping("/debitsimultanehydrant")
@Controller
public class DebitSimultaneHydrantController {

    @Autowired
    private DebitSimultaneHydrantService DebitSimultaneHydrantService;

    /*public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }*/

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                               final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                               final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                               @RequestParam(value = "filter", required = false) String filters) {


        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

        return new AbstractExtListSerializer<DebitSimultaneHydrant>("DebitSimultaneHydrant retrieved.") {

          @Override
          protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
            serializer.exclude("data.debit.hydrants").exclude("data.class").include("data.*");
            //serializer.exclude("data.class").include("data.*");
            return serializer;
          }

          @Override
          protected List<DebitSimultaneHydrant> getRecords() {
            return DebitSimultaneHydrantService.find(start, limit, itemSortList, itemFilterList);
          }

          @Override
          protected Long countRecords() {
            return
               Long.valueOf(DebitSimultaneHydrantService.count(itemFilterList));
          }

        }.serialize();
    }

}