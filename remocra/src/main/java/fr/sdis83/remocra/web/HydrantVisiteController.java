package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<HydrantVisite>("fr.sdis83.remocra.domain.remocra.HydrantVisite retrieved.") {

            @Override
            protected List<HydrantVisite> getRecords() {
                return HydrantVisite.findAllHydrantVisites();
            }

        }.serialize();
    }

}