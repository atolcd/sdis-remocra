package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneHydrant;

import java.util.List;

@RequestMapping("/debitsimultanehydrant")
@Controller
public class DebitSimultaneHydrantController {

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<DebitSimultaneHydrant>("fr.sdis83.remocra.domain.remocra.DebitSimultaneHydrant retrieved.") {

            @Override
            protected List<DebitSimultaneHydrant> getRecords() {
                return DebitSimultaneHydrant.findAllDebitSimultaneHydrants();
            }

        }.serialize();
    }

}