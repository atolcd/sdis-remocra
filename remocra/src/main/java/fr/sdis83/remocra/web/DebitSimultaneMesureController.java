package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.service.DebitSimultaneMesureService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneMesure;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

@RequestMapping("/debitsimultanemesure")
@Controller
public class DebitSimultaneMesureController {

    @Autowired
    private DebitSimultaneMesureService debitSimultaneMesureService;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                               final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                               final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                               @RequestParam(value = "filter", required = false) String filters) {


        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

        return new AbstractExtListSerializer<DebitSimultaneMesure>("DebitSimultaneMesure retrieved.") {

          @Override
          protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
            return new JSONSerializer().exclude("*.class").exclude("data.hydrants").include("data.*").exclude("*.class").transform(new GeometryTransformer(), Geometry.class);
            //return new JSONSerializer().exclude("*.class").include("data.*").transform(new GeometryTransformer(), Geometry.class);

          }

          @Override
          protected List<DebitSimultaneMesure> getRecords() {
            return debitSimultaneMesureService.find(start, limit, itemSortList, itemFilterList);
          }

          @Override
          protected Long countRecords() {
            return
               Long.valueOf(debitSimultaneMesureService.count(itemFilterList));
          }

        }.serialize();
    }

    /**
     * Mise à jour de plusieurs DebitSimultaneMesure à la fois
     */
    @RequestMapping(value = "/updatemany", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
    public ResponseEntity<java.lang.String> update(MultipartHttpServletRequest request) {
        String json = request.getParameter("mesures");
        Map<String, MultipartFile> files = request.getFileMap();
        try {
            debitSimultaneMesureService.updateMany(json, files);
            return new SuccessErrorExtSerializer(true, "DebitSimultaneMesure updated.").serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
    public ResponseEntity<java.lang.String> delete(final @RequestBody String json) {
        try {
            debitSimultaneMesureService.delete(json);
            return new SuccessErrorExtSerializer(true, "DebitSimultaneMesure supprimés").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

}