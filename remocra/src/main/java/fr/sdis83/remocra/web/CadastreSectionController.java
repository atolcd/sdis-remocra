package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.CadastreSection;
import fr.sdis83.remocra.service.CadastreSectionService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/cadastresection")
@Controller
public class CadastreSectionController {

    @Autowired
    private CadastreSectionService cadastreSectionService;

    @RequestMapping(value = "/numero", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listNumeroJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
            @RequestParam(value = "filter", required = false) String filters) {

        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);

        // Ajout du filtre sur le numéro
        if (query != null) {
            itemFilterList.add(new ItemFilter("numero", query));
        }
        // Ajout du tri par numéro si non précisé
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("numero", "ASC"));
        }

        return new AbstractExtListSerializer<CadastreSection>("CadastreSection retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.include("data.id").include("data.numero").exclude("*");
            }

            @Override
            protected List<CadastreSection> getRecords() {
                return cadastreSectionService.find(start, limit, sortList, itemFilterList);
            }

        }.serialize();
    }

}
