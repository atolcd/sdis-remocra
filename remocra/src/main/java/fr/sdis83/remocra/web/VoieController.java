package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Voie;
import fr.sdis83.remocra.service.VoieService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/voies")
@Controller
public class VoieController {

    @Autowired
    private VoieService voieService;

    @RequestMapping(value = "/mc", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listMotClassantOrNomJson(final @RequestParam(value = "page", required = false) Integer page,
             final @RequestParam(value = "withgeom", required = false,  defaultValue = "true") boolean withgeom,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
            @RequestParam(value = "filter", required = false) String filters) {

        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);

        // Ajout du filtre sur le mot classant (ou le nom)
        if (query != null & !ItemFilter.hasFilter(itemFilterList, "mc")) {
            itemFilterList.add(new ItemFilter("mc", query));
        }
        // Ajout du tri par nom si non précisé
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("nom", "ASC"));
        }

        return new AbstractExtListSerializer<Voie>("Voies retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.include("data.id").include("data.nom").include("data.motClassant").include("data.source").include(withgeom?"data.geometrie":"")
                    .include(withgeom?"data.commune.id": "").include(withgeom?"data.commune.code": "").include(withgeom?"data.commune.insee": "")
                    .exclude("data.commune.geometrie")
                    .exclude("*");
            }

            @Override
            protected List<Voie> getRecords() {
                return voieService.findVoiesByMotClassantOrNomLike(start, limit, sortList, itemFilterList);
            }

        }.serialize();
    }
}
