package fr.sdis83.remocra.web;


import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Gestionnaire;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.GestionnaireService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RequestMapping("/gestionnaire")
@Controller
public class GestionnaireController {

    @Autowired
    GestionnaireService service;

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<String> listJson() {
        return new AbstractExtListSerializer<Gestionnaire>("fr.sdis83.remocra.domain.remocra.Gestionnaire retrieved.") {

            @Override
            protected List<Gestionnaire> getRecords() {
                return Gestionnaire.findAllGestionnaires();
            }

        }.serialize();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
    public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
        try {
            final Gestionnaire attached = service.create(json, null);
            return new AbstractExtObjectSerializer<Gestionnaire>("Gestionnaire created") {
                @Override
                protected Gestionnaire getRecord() throws BusinessException {
                    return attached;
                }
            }.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }
}