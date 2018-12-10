package fr.sdis83.remocra.web;

import java.util.List;

import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.Geometry;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.service.AuthService;
import fr.sdis83.remocra.service.CommuneService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/communes")
@Controller
public class CommuneController {

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private CommuneService communeService;

    @RequestMapping(value = "/nom", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listNomJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "withgeom", required = false,  defaultValue = "true") boolean withgeom,
            final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
            final @RequestParam(value = "filter", required = false) String filters) {

        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final ItemSorting itemSorting = sortList.isEmpty() ? new ItemSorting("nom", "ASC") : sortList.get(0);

        return new AbstractExtListSerializer<Commune>("Communes retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.include("data.id").include("data.nom").include("data.insee").include("data.pprif").include("data.bbox").include(withgeom? "data.geometrie" : "").exclude("*");
            }

            @Override
            protected List<Commune> getRecords() {

                Geometry paramZoneGeom = null;
                // Si l'utilisateur est indentifié, vérification du passage en
                // paramètre de la zone de compétence
                if (AuthService.isUserAuthenticated()) {
                    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
                    for (ItemFilter itemFilter : itemFilterList) {
                        if ("zoneCompetence".equals(itemFilter.getFieldName())) {
                            paramZoneGeom = utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie();
                        } else if ("oldebGeom".equals(itemFilter.getFieldName())) {
                            return communeService.find(start, limit, sortList, itemFilterList);
                        }
                    }
                }

                return Commune.findCommunesByNomLike(start, limit, query, itemSorting, paramZoneGeom);
            }

        }.serialize();
    }

    @RequestMapping(value = "/xy", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> findCommuneByPoint(final @RequestParam(value = "srid", required = true) Integer srid,
            final @RequestParam(value = "wkt", required = true) String wkt) {
        return new AbstractExtListSerializer<Commune>("Communes retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.include("data.id").include("data.nom").include("data.insee").include("data.pprif").include("data.geometrie").exclude("*");
            }

            @Override
            protected List<Commune> getRecords() {
                return Commune.findCommunesByPoint(srid, wkt);
            }

        }.serialize();
    }
}
