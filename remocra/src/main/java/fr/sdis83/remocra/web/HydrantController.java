package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;

@RequestMapping("/hydrants")
@Controller
public class HydrantController {

    @Autowired
    private HydrantService hydrantService;

    @Autowired
    private ZoneCompetenceService zoneCompetenceService;

    @Autowired
    private UtilisateurService serviceUtilisateur;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS', 'READ')")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "sort", required = false) String sorts, final @RequestParam(value = "filter", required = false) String filters,
            final @RequestParam(value = "query", required = false) String query) {

        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

        if (query != null && !query.isEmpty()) {
            itemFilterList.add(new ItemFilter("query", query));
        }

        itemFilterList.add(new ItemFilter("zoneCompetence", "true"));

        return new AbstractExtListSerializer<Hydrant>("fr.sdis83.remocra.domain.remocra.Hydrant retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer()
                        .include("data.id", "data.numero", "data.natureNom", "data.natureCode","data.dateRecep", "data.code", "data.tourneeId", "data.dateReco", "data.dateContr", "data.hbe",
                                "data.jsonGeometrie", "data.dispoHbe","data.indispoTemp","data.dispoTerrestre", "data.debit").exclude("data.*", "*.class")
                        .transform(new GeometryTransformer(), Geometry.class);
            }

            @Override
            protected List<Hydrant> getRecords() {
                return hydrantService.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return hydrantService.count(itemFilterList);
            }

        }.serialize();
    }

    @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS', 'READ')")
    public ResponseEntity<java.lang.String> layer(final @RequestParam String bbox) {
        if (bbox == null || bbox.isEmpty()) {
            return FeatureUtil.getResponse(hydrantService.findAllHydrants());
        } else {
            return FeatureUtil.getResponse(hydrantService.findHydrantsByBBOX(bbox));
        }
    }

    @RequestMapping(value = "/desaffecter", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE', 'CREATE')")
    public ResponseEntity<java.lang.String> desaffecter(final @RequestBody String json) {
        Integer nbHydrant = hydrantService.desaffecter(json);
        return new SuccessErrorExtSerializer(true, nbHydrant.toString() + " hydrant(s) désaffecté(s)").serialize();

    }

    @RequestMapping(value = "/affecter", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE', 'CREATE')")
    public ResponseEntity<java.lang.String> affecter(final @RequestBody String json) {
        Integer nbHydrant = hydrantService.affecter(json);
        return new SuccessErrorExtSerializer(true, nbHydrant.toString() + " hydrant(s) affecté(s)").serialize();

    }

    @RequestMapping(value = "/checkdispo", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS', 'READ')")
    public ResponseEntity<java.lang.String> checkDispo(final @RequestParam(value = "id", required = false) Long id,
            final @RequestParam(value = "nature", required = false) Long nature, final @RequestParam(value = "commune", required = false) Long codeCommune,
            final @RequestParam(value = "num", required = false) String num, final @RequestParam(value = "geometrie", required = false) String geometrie) {
        String message = hydrantService.checkDispo(id, nature, codeCommune, (num == null || num.isEmpty() ? null : Integer.valueOf(num)), geometrie);
        return new SuccessErrorExtSerializer(message == null || message.isEmpty(), message).serialize();

    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS', 'CREATE')")
    @Transactional
    public ResponseEntity<java.lang.String> deleteHydrantDocument(@PathVariable("id") Long id) {
        try {
            hydrantService.deleteDocument(id);
            return new SuccessErrorExtSerializer(true, "Document supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "{id}/deplacer", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS', 'DELETE')")
    public ResponseEntity<java.lang.String> deplacer(final @PathVariable(value = "id") Long id, final @RequestParam(value = "geometrie") String geometrie,
            final @RequestParam(value = "srid") Integer srid) {

        try {
            JSONDeserializer<Point> deserializer = new JSONDeserializer<Point>();
            deserializer.use((String) null, new GeometryFactory());
            Point point = deserializer.deserialize(geometrie);
            point.setSRID(srid);
            Boolean result = zoneCompetenceService.check(point, serviceUtilisateur.getCurrentUtilisateur().getOrganisme().getZoneCompetence());
            if (!result) {
                return new SuccessErrorExtSerializer(result, "Déplacement du point d'eau non autorisé").serialize();
            }
            hydrantService.deplacer(id, point, srid);
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, "Problème survenu lors du déplacement du point d'eau").serialize();
        }
        return new SuccessErrorExtSerializer(true, "Point d'eau déplacé").serialize();

    }
}
