package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT_ROLES;
import static fr.sdis83.remocra.db.model.remocra.Tables.ROLE;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.domain.remocra.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ContactRepository {

  @Autowired DSLContext context;

  ContactRepository(DSLContext context) {
    this.context = context;
  }

  public ContactRepository() {}

  @Bean
  public ContactRepository contactRepository(DSLContext context) {
    return new ContactRepository(context);
  }

  /*
    appartenance : GESTIONNAIRE ou ORGANISME
    id : identifiant de l'organisme ou du gestionnaire
  */
  public List<fr.sdis83.remocra.domain.remocra.Contact> findAllContactById(
      String appartenance, String id) {
    Result<Record> contactRecord =
        context
            .selectFrom(CONTACT)
            .where(CONTACT.ID_APPARTENANCE.eq(id).and(CONTACT.APPARTENANCE.eq(appartenance)))
            .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

    List<fr.sdis83.remocra.domain.remocra.Contact> cr =
        new ArrayList<fr.sdis83.remocra.domain.remocra.Contact>();

    for (Record r : contactRecord) {
      fr.sdis83.remocra.domain.remocra.Contact contact =
          modelMapper.map(r, fr.sdis83.remocra.domain.remocra.Contact.class);

      Result<Record4<Long, String, String, Long>> ro =
          context
              .select(
                  ROLE.ID.as("id"),
                  ROLE.NOM.as("nom"),
                  ROLE.CODE.as("code"),
                  ROLE.THEMATIQUE.as("thematique"))
              .from(CONTACT)
              .join(CONTACT_ROLES)
              .on(CONTACT.ID.eq(CONTACT_ROLES.CONTACT).and(CONTACT.ID.eq(contact.getId())))
              .join(ROLE)
              .on(CONTACT_ROLES.ROLES.eq(ROLE.ID))
              .fetch();

      Set<Role> lstRoles = new HashSet<Role>();
      for (Record role : ro) {
        lstRoles.add(modelMapper.map(role, Role.class));
      }
      contact.setRoles(lstRoles);
      cr.add(contact);
    }
    return cr;
  }

  @Transactional
  public void createContactsFromJson(
      String jsonContacts, String appartenance, Long idAppartenance) {
    ArrayList<HashMap<String, Object>> contacts =
        jsonContacts == null || jsonContacts.isEmpty()
            ? new ArrayList<HashMap<String, Object>>()
            : new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(jsonContacts);
    for (HashMap<String, Object> contact : contacts) {
      fr.sdis83.remocra.domain.remocra.Contact c = new fr.sdis83.remocra.domain.remocra.Contact();
      c.setIdAppartenance(String.valueOf(idAppartenance));
      c.setAppartenance(appartenance);
      this.setUpInformation(c, contact);
    }
  }

  @Transactional
  public void updateContactsFromJson(
      String jsonContacts, String appartenance, Long idAppartenance) {

    ArrayList<HashMap<String, Object>> contacts =
        jsonContacts == null || jsonContacts.isEmpty()
            ? new ArrayList<HashMap<String, Object>>()
            : new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(jsonContacts);
    // Les contact existants déjà en base
    List<fr.sdis83.remocra.domain.remocra.Contact> lc =
        findAllContactById(appartenance, String.valueOf(idAppartenance));

    List<Long> contactToSave = new ArrayList();

    for (HashMap<String, Object> contact : contacts) {
      // On supprime les contacts et on ajoute les nouveaux
      if (contact.get("id") != null) {
        contactToSave.add(Long.valueOf(String.valueOf(contact.get("id"))));
      }

      fr.sdis83.remocra.domain.remocra.Contact c =
          contact.get("id") != null
              ? fr.sdis83.remocra.domain.remocra.Contact.findContact(
                  Long.valueOf(String.valueOf(contact.get("id"))))
              : new fr.sdis83.remocra.domain.remocra.Contact();
      // On supprime les anciens roles
      if (contact.get("id") != null) {
        Set<Role> rolesToRemove = c.getRoles();
        while (rolesToRemove.iterator().hasNext()) {
          c.getRoles().remove(rolesToRemove.iterator().next());
        }
      }
      c.setIdAppartenance(String.valueOf(idAppartenance));
      c.setAppartenance(appartenance);
      this.setUpInformation(c, contact);
    }
    for (fr.sdis83.remocra.domain.remocra.Contact dbContact : lc) {
      if (contactToSave.indexOf(dbContact.getId()) == -1) {
        dbContact.remove();
      }
    }
  }

  public void setUpInformation(
      fr.sdis83.remocra.domain.remocra.Contact c, HashMap<String, Object> contact) {

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
    c.setRoles(new HashSet<Role>());
    for (Integer r : rolesToAdd) {
      Role role = Role.findRole(Long.valueOf(r));
      c.getRoles().add(role);
    }
    c.persist();
  }

  /**
   * Récupère un contact
   *
   * @param idContact
   * @return un pojo contact
   */
  public Contact getContact(Long idContact) {
    return context.selectFrom(CONTACT).where(CONTACT.ID.eq(idContact)).fetchOneInto(Contact.class);
  }

  /**
   * Récupère les roles d'un contact
   *
   * @param idContact
   * @return une liste d'entier d'id roles du contact
   */
  public List<Long> getContactRolesById(Long idContact) {
    return context
        .select(CONTACT_ROLES.ROLES)
        .from(CONTACT_ROLES)
        .where(CONTACT_ROLES.CONTACT.eq(idContact))
        .fetchInto(Long.class);
  }

  /**
   * Créé un contact
   *
   * @param contact
   */
  public Long createContact(Contact contact) {
    return context
        .insertInto(
            CONTACT,
            CONTACT.APPARTENANCE,
            CONTACT.ID_APPARTENANCE,
            CONTACT.FONCTION,
            CONTACT.CIVILITE,
            CONTACT.NOM,
            CONTACT.PRENOM,
            CONTACT.NUMERO_VOIE,
            CONTACT.SUFFIXE_VOIE,
            CONTACT.LIEU_DIT,
            CONTACT.VOIE,
            CONTACT.CODE_POSTAL,
            CONTACT.VILLE,
            CONTACT.PAYS,
            CONTACT.TELEPHONE,
            CONTACT.EMAIL)
        .values(
            contact.getAppartenance(),
            contact.getIdAppartenance(),
            contact.getFonction(),
            contact.getCivilite(),
            contact.getNom(),
            contact.getPrenom(),
            contact.getNumeroVoie(),
            contact.getSuffixeVoie(),
            contact.getLieuDit(),
            contact.getVoie(),
            contact.getCodePostal(),
            contact.getVille(),
            contact.getPays(),
            contact.getTelephone(),
            contact.getEmail())
        .returning(CONTACT.ID)
        .fetchOne()
        .getValue(CONTACT.ID);
  }

  /**
   * Met à jour un contact
   *
   * @param idContact contact
   */
  public void updateContact(Long idContact, Contact contact) {
    context
        .update(CONTACT)
        .set(CONTACT.APPARTENANCE, contact.getAppartenance())
        .set(CONTACT.ID_APPARTENANCE, contact.getIdAppartenance())
        .set(CONTACT.FONCTION, contact.getFonction())
        .set(CONTACT.CIVILITE, contact.getCivilite())
        .set(CONTACT.NOM, contact.getNom())
        .set(CONTACT.PRENOM, contact.getPrenom())
        .set(CONTACT.NUMERO_VOIE, contact.getNumeroVoie())
        .set(CONTACT.SUFFIXE_VOIE, contact.getSuffixeVoie())
        .set(CONTACT.LIEU_DIT, contact.getLieuDit())
        .set(CONTACT.VOIE, contact.getVoie())
        .set(CONTACT.CODE_POSTAL, contact.getCodePostal())
        .set(CONTACT.VILLE, contact.getVille())
        .set(CONTACT.PAYS, contact.getPays())
        .set(CONTACT.TELEPHONE, contact.getTelephone())
        .set(CONTACT.EMAIL, contact.getEmail())
        .where(CONTACT.ID.eq(idContact))
        .execute();
  }

  public void deleteContact(Long idContact) {
    context.delete(CONTACT).where(CONTACT.ID.eq(idContact)).execute();
  }
}
