package fr.sdis83.remocra.web;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.factories.EnumObjectFactory;
import fr.sdis83.remocra.db.model.remocra.tables.ContactRoles;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.repository.ContactRolesRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/contact")
@Controller
public class ContactController {

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactRolesRepository contactRolesRepository;

    private final Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/{idappartenance}", method = RequestMethod.GET,headers = "Accept=application/json")
    public ResponseEntity<String> listJson(@PathVariable("idappartenance") final String idappartenance,
       final @RequestParam(value = "appartenance", required = false) String appartenance) {
        return new AbstractExtListSerializer<fr.sdis83.remocra.domain.remocra.Contact>("fr.sdis83.remocra.domain.remocra.Contact retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer().include("data.roles")
                        .exclude("data.roles.thematique", "data.roles.actif", "data.roles.version")
                        .exclude("*.class");
            }

            @Override
            protected List<fr.sdis83.remocra.domain.remocra.Contact> getRecords() {
                return contactRepository.findAllContactById(appartenance,idappartenance);
            }

        }.serialize();
    }

    @RequestMapping(value = "/contactInfos/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<String> getContact(@PathVariable("id") Long id) {
        return new AbstractExtObjectSerializer<Contact>("Get Contact Informations by id") {
            @Override
            protected Contact getRecord() throws BusinessException {
                return contactRepository.getContact(id);
            }
        }.serialize();
    }

    @RequestMapping(value = "/contactRoles/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<String> getContactRolesById(@PathVariable("id") Long id) {
        return new AbstractExtListSerializer<Long>("Get Contact Informations by id") {
            @Override
            protected List<Long> getRecords() throws BusinessException {
                return contactRepository.getContactRolesById(id);
            }
        }.serialize();
    }

    @RequestMapping(value = "/updateContact/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> updateContact(HttpServletRequest request, @PathVariable("id") final Long idContact) {
        try{
            String contact = request.getParameter("contact");
            Contact objContact = new JSONDeserializer<Contact>()
                    .use(null, Contact.class).deserialize(contact);
            contactRepository.updateContact(idContact, objContact);
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "update contact infos").serialize();
        }
        try{
            String role = request.getParameter("role");
            contactRolesRepository.deleteContactRolesById(idContact);
            List<String> listRolesS = new ArrayList<String>(Arrays.asList(role.split(",")));
            System.out.println(role.isEmpty());
            if(!role.isEmpty()) {
                for (String s : listRolesS) contactRolesRepository.createContactRoles(idContact, Long.valueOf(s));
            }
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "update contact roles").serialize();
        }
        return new SuccessErrorExtSerializer(true, "Le contact et ses roles ont été mis à jour.").serialize();
    }

    @RequestMapping(value = "/createContact", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> createContact(HttpServletRequest request){
        Long idContact_ = null;
        try{
            String contact = request.getParameter("contact");
            Contact objContact = new JSONDeserializer<Contact>()
                    .use(null, Contact.class).deserialize(contact);
            idContact_ = contactRepository.createContact(objContact);
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "create contact infos").serialize();
        }
        try{
            String role = request.getParameter("role");
            if(!role.isEmpty()) {
                List<String> listRolesS = new ArrayList<String>(Arrays.asList(role.split(",")));
                for (String s : listRolesS) contactRolesRepository.createContactRoles(idContact_, Long.valueOf(s));
            }
        }catch (Exception e){
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "create contact roles").serialize();
        }
        return new SuccessErrorExtSerializer(true, "Le contact et ses roles ont été créés.").serialize();
    }

    @RequestMapping(value = "deleteContact/{id}", method = RequestMethod.DELETE, headers = "Accept=Application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> deleteContact(@PathVariable("id") final Long idContact){
        try{
            contactRolesRepository.deleteContactRolesById(idContact);
        }catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "delete contact roles").serialize();
        }
        try{
            contactRepository.deleteContact(idContact);
        }catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, "delete contact infos").serialize();
        }

        return new SuccessErrorExtSerializer(true, "Le contact et ses roles ont été supprimés").serialize();
    }
}