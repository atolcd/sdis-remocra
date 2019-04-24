package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAspiration;

import java.util.List;

@RequestMapping("/typehydrantaspiration")
@Controller
public class TypeHydrantAspirationController {

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<TypeHydrantAspiration>("fr.sdis83.remocra.domain.remocra.TypeHydrantAspiration retrieved.") {

            @Override
            protected List<TypeHydrantAspiration> getRecords() {
                return TypeHydrantAspiration.findAllTypeHydrantAspirations();
            }

        }.serialize();
    }

}