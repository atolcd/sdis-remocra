package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultane;

import java.util.List;

@RequestMapping("/debitsimultane")
@Controller
public class DebitSimultaneController {

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<DebitSimultane>("fr.sdis83.remocra.domain.remocra.DebitSimultane retrieved.") {

            @Override
            protected List<DebitSimultane> getRecords() {
                return DebitSimultane.findAllDebitSimultanes();
            }

        }.serialize();
    }

}