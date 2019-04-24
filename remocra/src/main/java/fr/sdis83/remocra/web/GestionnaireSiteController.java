package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.GestionnaireSite;

import java.util.List;

@RequestMapping("/gestionnairesite")
@Controller
public class GestionnaireSiteController {

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<GestionnaireSite>("fr.sdis83.remocra.domain.remocra.GestionnaireSite retrieved.") {

            @Override
            protected List<GestionnaireSite> getRecords() {
                return GestionnaireSite.findAllGestionnaireSites();
            }

        }.serialize();
    }

}