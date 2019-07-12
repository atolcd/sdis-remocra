package fr.sdis83.remocra.web;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Gestionnaire;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.ContactService;
import fr.sdis83.remocra.service.GestionnaireService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/gestionnaire")
@Controller
public class GestionnaireController {

    @Autowired
    GestionnaireService service;

    @Autowired
    ContactService contactService;

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", method = RequestMethod.GET,headers = "Accept=application/json")
    public ResponseEntity<String> listJson() {
        return new AbstractExtListSerializer<Gestionnaire>("fr.sdis83.remocra.domain.remocra.Gestionnaire retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer().exclude("*.class");
            }

            @Override
            protected List<Gestionnaire> getRecords() {
                return Gestionnaire.findAllGestionnaires();
            }

        }.serialize();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,headers = "Accept=application/json")
    public ResponseEntity<String> listJson(@PathVariable("id") final Long id) {
        return new AbstractExtObjectSerializer<Gestionnaire>("fr.sdis83.remocra.domain.remocra.Gestionnaire retrieved.") {

            @Override
            protected Gestionnaire getRecord() throws BusinessException {
                return Gestionnaire.findGestionnaire(id);
            }

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer().exclude("*.class");
            }

        }.serialize();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
    public ResponseEntity<java.lang.String> create(HttpServletRequest request) {
        try {
            String gestionnaire = request.getParameter("gestionnaire");
            String contactsJson = request.getParameter("contacts");

            final Gestionnaire attached = service.create(gestionnaire, null);

            contactService.createContactsFromJson(contactsJson, "GESTIONNAIRE", attached.getId());


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

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_GESTIONNAIRE_C')")
    public ResponseEntity<java.lang.String> update(HttpServletRequest request, @PathVariable("id") final Long id) {
        try {
            String gestionnaire = request.getParameter("gestionnaire");
            String contactsJson = request.getParameter("contacts");

            final Gestionnaire attached = service.update(id , gestionnaire, null);
            contactService.updateContactsFromJson(contactsJson, "GESTIONNAIRE",id);

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