package fr.sdis83.remocra.web;

import fr.sdis83.remocra.service.HydrantAspirationService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.HydrantAspiration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequestMapping("/hydrantaspiration")
@Controller
public class HydrantAspirationController {

    @Autowired
    private HydrantAspirationService hydrantAspirationService;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
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
}