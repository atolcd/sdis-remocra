package fr.sdis83.remocra.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.Metadonnee;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/metadonnees")
@Controller
public class MetadonneeController {

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<Metadonnee>("fr.sdis83.remocra.domain.remocra.Metadonnee retrieved.") {

            @Override
            protected List<Metadonnee> getRecords() {
                return Metadonnee.findAllMetadonnees();
            }

        }.serialize();
    }

}
