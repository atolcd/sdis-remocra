package fr.sdis83.remocra.web;

import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantVisiteService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.HydrantVisite;

import java.util.List;

@RequestMapping("/hydrantvisite")
@Controller
public class HydrantVisiteController{

    @Autowired
    private HydrantVisiteService hydrantVisiteService;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                               final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                               final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                               @RequestParam(value = "filter", required = false) String filters) {


        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

        return new AbstractExtListSerializer<HydrantVisite>("HydrantVisite retrieved.") {

          @Override
          protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
            serializer.exclude("*.class").exclude("data.hydrant").include("data.*").exclude("*.class");

            return serializer;
          }

          @Override
          protected List<HydrantVisite> getRecords() {
            return hydrantVisiteService.find(start, limit, itemSortList, itemFilterList);
          }

          @Override
          protected Long countRecords() {
            return
               Long.valueOf(hydrantVisiteService.count(itemFilterList));
          }

        }.serialize();
    }

    /**
     * Mise à jour de plusieurs HydrantVisite à la fois
     * @param json Un tableau JSON des informations des HydrantVisite
     */
    @RequestMapping(value = "/updatemany", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> update(final @RequestBody String json) {
        try {
            hydrantVisiteService.updateMany(json);
            return new SuccessErrorExtSerializer(true, "HydrantVisite updated.").serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
        try {
            final HydrantVisite attached = hydrantVisiteService.create(json, null);
            return new AbstractExtObjectSerializer<HydrantVisite>("HydrantVisite created") {
                @Override
                protected HydrantVisite getRecord() throws BusinessException {
                    return attached;
                }
            }.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> delete(final @RequestBody String json) {
        try {
            hydrantVisiteService.delete(json);
            return new SuccessErrorExtSerializer(true, "HydrantVisite supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

}