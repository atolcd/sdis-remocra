package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.Contact;
import fr.sdis83.remocra.domain.remocra.Role;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ContactService extends AbstractService<Contact> {

    public ContactService() {
        super(Contact.class);
    }

     @Transactional
    public void createContactsFromJson(String jsonContacts, String appartenance, Long idAppartenance){
        ArrayList<HashMap<String, Object>> contacts = jsonContacts == null || jsonContacts.isEmpty() ? new ArrayList<HashMap<String, Object>>()
            : new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(jsonContacts);
        for(HashMap<String, Object> contact : contacts){
            Contact c =  new Contact();
            c.setIdAppartenance(String.valueOf(idAppartenance));
            c.setAppartenance(appartenance);
            this.setUpInformation(c, contact);
        }

    }


    @Transactional
    public void updateContactsFromJson(String jsonContacts, String appartenance, Long idAppartenance){

        ArrayList<HashMap<String, Object>> contacts = jsonContacts == null || jsonContacts.isEmpty() ? new ArrayList<HashMap<String, Object>>()
            : new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(jsonContacts);
        //Les contact existants déjà en base
        List<Contact> lc = Contact.findAllContactsByIdAppartenance(String.valueOf(idAppartenance));

        List<Long> contactToSave = new ArrayList();

        for(HashMap<String, Object> contact : contacts){
            // On supprime les contacts et on ajoute les nouveaux
            if(contact.get("id") != null){
                contactToSave.add(Long.valueOf(String.valueOf(contact.get("id"))));
            }

            Contact c  = contact.get("id") != null ? Contact.findContact(Long.valueOf(String.valueOf(contact.get("id")))) : new Contact();
            //On supprime les anciens roles
            if(contact.get("id") != null ){
                Set<Role> rolesToRemove = c.getRoles();
                while (rolesToRemove.iterator().hasNext()) {
                    c.getRoles().remove(rolesToRemove.iterator().next());
                }
            }
            c.setIdAppartenance(String.valueOf(idAppartenance));
            c.setAppartenance(appartenance);
            this.setUpInformation(c, contact);
        }
        for (Contact dbContact : lc){
            if(contactToSave.indexOf(dbContact.getId()) == -1){
                dbContact.remove();
            }
        }


    }

    public void setUpInformation(Contact c, HashMap<String, Object> contact){

        c.setCivilite(String.valueOf(contact.get("civilite")));
        c.setFonction(String.valueOf(contact.get("fonction")));
        c.setNom(String.valueOf(contact.get("nom")));
        c.setPrenom(String.valueOf(contact.get("prenom")));
        c.setNumeroVoie(String.valueOf(contact.get("numeroVoie")));
        c.setSuffixeVoie(String.valueOf(contact.get("suffixeVoie")));
        c.setLieuDit(String.valueOf(contact.get("lieuDit")));
        c.setVoie(String.valueOf(contact.get("voie")));
        c.setCodePostal(String.valueOf(contact.get("codePostal")));
        c.setVille(String.valueOf(contact.get("ville")));
        c.setPays(String.valueOf(contact.get("pays")));
        c.setTelephone(String.valueOf(contact.get("telephone")));
        c.setEmail(String.valueOf(contact.get("email")));
        List<Integer> rolesToAdd = (List<Integer>) contact.get("roles");
        c.setRoles( new HashSet<Role>());
        for(Integer r : rolesToAdd){
            Role role = Role.findRole(Long.valueOf(r));
            c.getRoles().add(role);
        }
        c.persist();

    }

}