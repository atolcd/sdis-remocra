package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.OldebVisite;
import fr.sdis83.remocra.service.OldebVisiteService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/oldebvisite")
@Controller
public class OldebVisiteController extends AbstractServiceableController<OldebVisiteService, OldebVisite> {

    @Autowired
    private OldebVisiteService service;

    @Override
    protected OldebVisiteService getService() {
        return service;
    }

    /**
     * Retour de la liste des visites
     */
    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
    @PreAuthorize("hasRight('OLDEB', 'READ')")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
            @RequestParam(value = "filter", required = false) String filters) {

        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        // Ajout du filtre sur l'oldeb
        if (query != null) {
            itemFilterList.add(new ItemFilter("oldeb", query));
        }
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        // Ajout du tri par défaut par ordre croissant
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("oldeb", "ASC"));
        }

        return new AbstractExtListSerializer<OldebVisite>("OldebVisite retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                serializer.include("data.nomAvis").include("data.totalAnomalies").include("data.oldebVisiteSuites").include("data.oldebVisiteDocuments")
                        .include("data.typeOldebAnomalies").include("data.nomAction").include("data.nomDebAcces").include("data.nomDebParcelle");

                return serializer.include("total").include("message");
            }

            @Override
            protected List<OldebVisite> getRecords() {
                return service.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return service.count(itemFilterList);
            }

        }.serialize();
    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('OLDEB', 'DELETE')")
    @Transactional
    public ResponseEntity<java.lang.String> deleteOldebVisiteDocument(@PathVariable("id") Long id) {
        try {
            service.deleteDocument(id);
            return new SuccessErrorExtSerializer(true, "Document supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

}
