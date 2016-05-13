package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/communes")
@Controller
public class CommuneController {

    @RequestMapping(value = "/nom", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listNomJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "query", required = false) String query) {
        return new AbstractExtListSerializer<Commune>("Communes retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.include("data.id").include("data.nom").include("data.insee").include("data.pprif").include("data.geometrie").exclude("*");
            }

            @Override
            protected List<Commune> getRecords() {
                return Commune.findCommunesByNomLike(start, limit, query);
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
