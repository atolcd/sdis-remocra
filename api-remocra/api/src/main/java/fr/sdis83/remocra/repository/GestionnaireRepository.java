package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.*;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class GestionnaireRepository {
  private final DSLContext context;

  @Inject
  public GestionnaireRepository(DSLContext context) {
    this.context = context;
  }

  public boolean checkExist(Long idGestionnaire) {
    return context.fetchExists(
        context.selectFrom(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(idGestionnaire)));
  }

  public boolean checkContactExist(Long idContact) {
    return context.fetchExists(context.selectFrom(CONTACT).where(CONTACT.ID.eq(idContact)));
  }

  public boolean checkRoleExist(Long idRole) {
    return context.fetchExists(context.selectFrom(ROLE).where(ROLE.ID.eq(idRole)));
  }

  public Long insertGestionnaire(Gestionnaire gestionnaire) {
    return context
        .insertInto(GESTIONNAIRE)
        .set(GESTIONNAIRE.ACTIF, gestionnaire.getActif())
        .set(GESTIONNAIRE.NOM, gestionnaire.getNom())
        .set(GESTIONNAIRE.CODE, gestionnaire.getCode())
        .returning(GESTIONNAIRE.ID)
        .fetchOne()
        .getValue(GESTIONNAIRE.ID);
  }

  public void updateGestionnaire(Gestionnaire gestionnaire) {
    context
        .update(GESTIONNAIRE)
        .set(GESTIONNAIRE.ACTIF, gestionnaire.getActif())
        .set(GESTIONNAIRE.NOM, gestionnaire.getNom())
        .set(GESTIONNAIRE.CODE, gestionnaire.getCode())
        .where(GESTIONNAIRE.ID.eq(gestionnaire.getId()))
        .execute();
  }

  public Long insertContact(Contact contact) {
    return context
        .insertInto(CONTACT)
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
        .set(CONTACT.ID_GESTIONNAIRE_SITE, contact.getIdGestionnaireSite())
        .returning(CONTACT.ID)
        .fetchOne()
        .getValue(CONTACT.ID);
  }

  public void updateContact(Contact contact) {
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
        .set(CONTACT.ID_GESTIONNAIRE_SITE, contact.getIdGestionnaireSite())
        .where(CONTACT.ID.eq(contact.getId()))
        .execute();
  }

  public Long getGestionnaireSiteContact(Long idContact) {
    return context
        .select(CONTACT.ID_GESTIONNAIRE_SITE)
        .from(CONTACT)
        .where(CONTACT.ID.eq(idContact))
        .fetchOneInto(Long.class);
  }

  // Map<idContact, List<IdRole>>
  public Map<Long, List<Long>> getRolesByContact(List<Long> idsContact) {
    return context
        .select(CONTACT.ID, CONTACT_ROLES.ROLES)
        .from(CONTACT)
        .leftJoin(CONTACT_ROLES)
        .on(CONTACT_ROLES.CONTACT.eq(CONTACT.ID))
        .where(CONTACT.ID.in(idsContact))
        .fetchGroups(CONTACT.ID, CONTACT_ROLES.ROLES);
  }

  public void insertContactRole(Long idContact, Long idRole) {
    context
        .insertInto(CONTACT_ROLES)
        .set(CONTACT_ROLES.CONTACT, idContact)
        .set(CONTACT_ROLES.ROLES, idRole)
        .onConflictDoNothing()
        .execute();
  }

  public void deleteContactRole(Long idContact, Long idRole) {
    context
        .deleteFrom(CONTACT_ROLES)
        .where(CONTACT_ROLES.CONTACT.eq(idContact))
        .and(CONTACT_ROLES.ROLES.eq(idRole))
        .execute();
  }
}
