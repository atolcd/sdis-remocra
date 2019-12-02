package fr.sdis83.remocra.web;


import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Contact;
import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/contact")
@Controller
public class ContactController {

    @Autowired
    ContactRepository contactRepository;

    @RequestMapping(value = "/{idappartenance}", method = RequestMethod.GET,headers = "Accept=application/json")
    public ResponseEntity<String> listJson(@PathVariable("idappartenance") final String idappartenance) {
        return new AbstractExtListSerializer<Contact>("fr.sdis83.remocra.domain.remocra.Contact retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer().include("data.roles").exclude("data.roles.thematique","*.class");
            }

            @Override
            protected List<Contact> getRecords() {
                return contactRepository.findAllContactById("ORGANISME",idappartenance);
            }

        }.serialize();
    }
}