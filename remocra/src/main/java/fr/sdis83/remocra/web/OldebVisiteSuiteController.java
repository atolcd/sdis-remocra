package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.OldebVisiteSuite;
import fr.sdis83.remocra.service.OldebVisiteSuiteService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/oldebvisitesuite")
@Controller
public class OldebVisiteSuiteController extends AbstractServiceableController<OldebVisiteSuiteService, OldebVisiteSuite> {

    @Autowired
    private OldebVisiteSuiteService service;

    @Override
    protected OldebVisiteSuiteService getService() {
        return service;
    }

    /**
     * Retour de la liste des visitesuites
     */
    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
    @PreAuthorize("hasRight('OLDEB_R')")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
            @RequestParam(value = "filter", required = false) String filters) {

        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        // Ajout du filtre sur la visite
        if (query != null) {
            itemFilterList.add(new ItemFilter("visite", query));
        }
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        // Ajout du tri par défaut par ordre croissant
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("visite", "ASC"));
        }

        return new AbstractExtListSerializer<OldebVisiteSuite>("OldebVisiteSuite retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                serializer.include("data.nomSuite");

                return serializer.include("total").include("message");
            }

            @Override
            protected List<OldebVisiteSuite> getRecords() {
                return service.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return service.count(itemFilterList);
            }

        }.serialize();
    }
}
