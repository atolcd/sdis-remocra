package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT_ROLES;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GestionnaireRepository {
  @Autowired DSLContext context;

  GestionnaireRepository(DSLContext context) {
    this.context = context;
  }

  public GestionnaireRepository() {}

  @Bean
  public GestionnaireRepository gestionnaireRepository(DSLContext context) {
    return new GestionnaireRepository(context);
  }

  public List<Gestionnaire> findAllGestionnaires() {
    return context.selectFrom(GESTIONNAIRE).fetchInto(Gestionnaire.class);
  }

  /**
   * Retourne la liste des numéros d'hydrants d'un gestionnaire
   *
   * @param idGestionnaire
   * @return une liste des numéros d'hydrants
   */
  public List<String> getHydrantWithIdGestionnaire(Long idGestionnaire) {
    return context
        .selectDistinct(HYDRANT.NUMERO)
        .from(HYDRANT)
        .where(HYDRANT.GESTIONNAIRE.eq(idGestionnaire))
        .fetchInto(String.class);
  }

  /**
   * Retourne la liste des id d'hydrants d'un gestionnaire
   *
   * @param idGestionnaire
   * @return une liste des id d'hydrants
   */
  public List<Long> getIdHydrantWithIdGestionnaire(Long idGestionnaire) {
    return context
        .selectDistinct(HYDRANT.ID)
        .from(HYDRANT)
        .where(HYDRANT.GESTIONNAIRE.eq(idGestionnaire))
        .fetchInto(Long.class);
  }

  /**
   * Récupère un gestionnaire
   *
   * @param idGestionnaire
   * @return un pojo gestionnaire
   */
  public Gestionnaire getGestionnaire(Long idGestionnaire) {
    return context
        .selectFrom(GESTIONNAIRE)
        .where(GESTIONNAIRE.ID.eq(idGestionnaire))
        .fetchOneInto(Gestionnaire.class);
  }

  /**
   * Retourne la liste des identifiants gestionnaire
   *
   * @return une liste de Long d'idGestionnaire
   */
  public List<Long> getGestionnaireIds() {
    return context
        .selectDistinct(GESTIONNAIRE.ID)
        .from(GESTIONNAIRE)
        .orderBy(GESTIONNAIRE.ID)
        .fetchInto(Long.class);
  }

  /**
   * Retourne la liste des codes gestionnaire n'appartenant pas au gestionnaire spécifié
   *
   * @return une liste de String de code Gestionnaire (SIREN)
   */
  public List<String> getOtherGestionnaireCodes(Long idGestionnaire) {
    return context
        .selectDistinct(GESTIONNAIRE.CODE)
        .from(GESTIONNAIRE)
        .where(GESTIONNAIRE.CODE.isNotNull())
        .and(
            idGestionnaire == null ? DSL.trueCondition() : GESTIONNAIRE.ID.notEqual(idGestionnaire))
        .fetchInto(String.class);
  }

  /**
   * Retourne la liste des codes gestionnaire
   *
   * @return une liste de String de code Gestionnaire (SIREN)
   */
  public List<String> getGestionnaireCodes() {
    return getOtherGestionnaireCodes(null);
  }

  /**
   * Créé un gestionnaire
   *
   * @param gestionnaire
   */
  public void createGestionnaire(Gestionnaire gestionnaire) {
    context
        .insertInto(GESTIONNAIRE, GESTIONNAIRE.NOM, GESTIONNAIRE.CODE, GESTIONNAIRE.ACTIF)
        .values(gestionnaire.getNom(), gestionnaire.getCode(), gestionnaire.getActif())
        .execute();
  }

  /**
   * Met à jour un gestionnaire
   *
   * @param idGestionnaire gestionnaire
   */
  public void updateGestionnaire(Long idGestionnaire, Gestionnaire gestionnaire) {
    context
        .update(GESTIONNAIRE)
        .set(GESTIONNAIRE.NOM, gestionnaire.getNom())
        .set(GESTIONNAIRE.CODE, gestionnaire.getCode())
        .set(GESTIONNAIRE.ACTIF, gestionnaire.getActif())
        .where(GESTIONNAIRE.ID.eq(idGestionnaire))
        .execute();
  }

  /**
   * Récupère tous les idContact d'un gestionnaire
   *
   * @param idGestionnaire
   * @return la liste des identifiants des contacts
   */
  public List<Long> getContactGestionnaire(Long idGestionnaire) {
    return context
        .select(CONTACT.ID)
        .from(CONTACT)
        .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
        .and(CONTACT.ID_APPARTENANCE.eq(idGestionnaire.toString()))
        .fetchInto(Long.class);
  }

  /**
   * Supprime les liens entre les contacts et les rôles
   *
   * @param listIdContact
   */
  public void deleteContactRole(List<Long> listIdContact) {
    context.deleteFrom(CONTACT_ROLES).where(CONTACT_ROLES.CONTACT.in(listIdContact)).execute();
  }

  /**
   * Supprime les contacts d'un gestionnaire
   *
   * @param idGestionnaire
   */
  public void deleteContactGestionnaire(Long idGestionnaire) {
    context
        .deleteFrom(CONTACT)
        .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
        .and(CONTACT.ID_APPARTENANCE.eq(idGestionnaire.toString()))
        .execute();
  }

  /**
   * Supprime le gestionnaire
   *
   * @param idGestionnaire
   */
  public void deleteGestionnaire(Long idGestionnaire) {
    context.deleteFrom(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(idGestionnaire)).execute();
  }
}
