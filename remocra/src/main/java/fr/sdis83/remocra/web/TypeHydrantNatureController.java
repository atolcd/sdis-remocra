package fr.sdis83.remocra.web;

import fr.sdis83.remocra.service.TypeHydrantNatureService;
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
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;

import java.util.List;

@RequestMapping("/typehydrantnatures")
@Controller
public class TypeHydrantNatureController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    public TypeHydrantNatureController() {
        super(TypeHydrantNature.class);
    }

    /*public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }*/

    @Autowired
    private TypeHydrantNatureService service;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                                           final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                                           final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                                           @RequestParam(value = "filter", required = false) String filters) {


        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

        return new AbstractExtListSerializer<TypeHydrantNature>("TypeHydrantNature retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                serializer.include("data.*");

                return serializer;
            }

            @Override
            protected List<TypeHydrantNature> getRecords() {
                return service.find(start, limit, itemSortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return Long.valueOf(service.count(itemFilterList));
            }

        }.serialize();

    }
}
