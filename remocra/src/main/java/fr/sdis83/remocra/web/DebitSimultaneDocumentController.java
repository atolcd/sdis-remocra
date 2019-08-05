package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultaneDocument;

import java.util.List;

@RequestMapping("/debitsimultanedocument")
@Controller
public class DebitSimultaneDocumentController {

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<DebitSimultaneDocument>("fr.sdis83.remocra.domain.remocra.DebitSimultaneDocument retrieved.") {

            @Override
            protected List<DebitSimultaneDocument> getRecords() {
                return DebitSimultaneDocument.findAllDebitSimultaneDocuments();
            }

        }.serialize();
    }

}