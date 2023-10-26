package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE_SITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.val;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.GestionnaireSite;
import fr.sdis83.remocra.usecase.gestionnaireSite.ComboGestionnaireSiteInfos;
import fr.sdis83.remocra.usecase.gestionnaireSite.GestionnaireSiteInfos;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GestionnaireSiteRepository {
  @Autowired DSLContext context;

  GestionnaireSiteRepository(DSLContext context) {
    this.context = context;
  }

  public GestionnaireSiteRepository() {}

  @Bean
  public GestionnaireSiteRepository siteRepository(DSLContext context) {
    return new GestionnaireSiteRepository(context);
  }

  /**
   * Récupère un objet gestionnaire_site à partir de son id
   *
   * @param idGestionnaireSite
   * @return un objet de type Gestionnaire_Site
   */
  public GestionnaireSite getGestionnaireSiteById(Long idGestionnaireSite) {
    return context
        .selectFrom(GESTIONNAIRE_SITE)
        .where(GESTIONNAIRE_SITE.ID.eq(idGestionnaireSite))
        .fetchOneInto(GestionnaireSite.class);
  }

  /**
   * Récupère tous les gestionnaire_site d'un gestionnaire
   *
   * @param idGestionnaire
   * @return une liste d'objet de type Gestionnaire_Site
   */
  public List<ComboGestionnaireSiteInfos> getComboSiteByGestionnaireId(Long idGestionnaire) {
    return context
        .select(
            GESTIONNAIRE_SITE.ID.as("idGestionnaireSite"),
            GESTIONNAIRE_SITE.NOM.as("nomGestionnaireSite"))
        .from(GESTIONNAIRE_SITE)
        .where(GESTIONNAIRE_SITE.ID_GESTIONNAIRE.eq(idGestionnaire))
        .and(GESTIONNAIRE_SITE.ACTIF)
        .fetchInto(ComboGestionnaireSiteInfos.class);
  }

  /**
   * Récupère toutes les informations de tous les gestionnaire_site
   *
   * @return une liste d'objet GestionnaireSiteInfos
   */
  public List<GestionnaireSiteInfos> getListGestionnaireSiteInfos() {
    return context
        .select(
            GESTIONNAIRE_SITE.ID.as("gestionnaireSite.id"),
            GESTIONNAIRE_SITE.NOM.as("gestionnaireSite.nom"),
            GESTIONNAIRE_SITE.CODE.as("gestionnaireSite.code"),
            GESTIONNAIRE_SITE.ACTIF.as("gestionnaireSite.actif"),
            GESTIONNAIRE_SITE.ID_GESTIONNAIRE.as("gestionnaireSite.id_gestionnaire"),
            GESTIONNAIRE_SITE.GEOMETRIE.as("gestionnaireSite.geometrie"),
            GESTIONNAIRE.NOM.as("gestionnaireName"))
        .from(GESTIONNAIRE_SITE)
        .leftOuterJoin(GESTIONNAIRE)
        .on(GESTIONNAIRE.ID.eq(GESTIONNAIRE_SITE.ID_GESTIONNAIRE))
        .fetchInto(GestionnaireSiteInfos.class);
  }

  /**
   * Récupère la liste de tous les gestionnaire_site
   *
   * @return une liste d'id de GestionnaireSite
   */
  public List<Long> getGestionnaireSiteIdList() {
    return context.select(GESTIONNAIRE_SITE.ID).from(GESTIONNAIRE_SITE).fetchInto(Long.class);
  }

  /**
   * Met à jour un gestionnaire_site
   *
   * @param idGestionnaireSite
   */
  public void updateGestionnaireSite(Long idGestionnaireSite, GestionnaireSite gestionnaireSite) {
    context
        .update(GESTIONNAIRE_SITE)
        .set(GESTIONNAIRE_SITE.NOM, gestionnaireSite.getNom())
        .set(GESTIONNAIRE_SITE.CODE, gestionnaireSite.getCode())
        .set(GESTIONNAIRE_SITE.ACTIF, gestionnaireSite.getActif())
        .set(GESTIONNAIRE_SITE.ID_GESTIONNAIRE, gestionnaireSite.getIdGestionnaire())
        .where(GESTIONNAIRE_SITE.ID.eq(idGestionnaireSite))
        .execute();
  }

  /**
   * Supprime le gestionnaire_site
   *
   * @param idGestionnaireSite
   */
  public void deleteGestionnaireSite(Long idGestionnaireSite) {
    context
        .deleteFrom(GESTIONNAIRE_SITE)
        .where(GESTIONNAIRE_SITE.ID.eq(idGestionnaireSite))
        .execute();
  }

  /**
   * Récupère tous les gestionnaire_site d'un gestionnaire
   *
   * @param idGestionnaire
   * @return la liste des identifiants des gestionnaire_site
   */
  public List<Long> getGestionnaireSiteByGestionnaireId(Long idGestionnaire) {
    return context
        .select(GESTIONNAIRE_SITE.ID)
        .from(GESTIONNAIRE_SITE)
        .where(GESTIONNAIRE_SITE.ID_GESTIONNAIRE.eq(idGestionnaire))
        .fetchInto(Long.class);
  }

  /**
   * Met à jour la FK Gestionnaire dans Gestionnaire_Site
   *
   * @param idGestionnaireSite
   * @param newIdGestionnaire
   */
  public void setGestionnaireIdInGestionnaireSite(Long idGestionnaireSite, Long newIdGestionnaire) {
    context
        .update(GESTIONNAIRE_SITE)
        .set(GESTIONNAIRE_SITE.ID_GESTIONNAIRE, newIdGestionnaire)
        .where(GESTIONNAIRE_SITE.ID.eq(idGestionnaireSite))
        .execute();
  }

  /**
   * Récupère la liste des numéros d'hydrants d'un gestionnaire_site
   *
   * @param idGestionnaireSite
   * @return une liste de numéros d'hydrants
   */
  public List<String> getHydrantWithIdGestionnaireSite(Long idGestionnaireSite) {
    return context
        .selectDistinct(HYDRANT.NUMERO)
        .from(HYDRANT)
        .where(HYDRANT.GESTIONNAIRE_SITE.eq(idGestionnaireSite))
        .fetchInto(String.class);
  }

  /**
   * Retourne la liste des id d'hydrants d'un gestionnaire_site
   *
   * @param idGestionnaireSite
   * @return une liste des id d'hydrants
   */
  public List<Long> getIdHydrantWithIdGestionnaireSite(Long idGestionnaireSite) {
    return context
        .selectDistinct(HYDRANT.ID)
        .from(HYDRANT)
        .where(HYDRANT.GESTIONNAIRE_SITE.eq(idGestionnaireSite))
        .fetchInto(Long.class);
  }

  /**
   * Récupère la liste des contacts liés à un gestionnaire_site
   *
   * @param idGestionnaireSite
   * @return une liste de civilités, noms, prenoms, fonctions de contacts
   */
  public List<String> getContactWithIdGestionnaireSite(Long idGestionnaireSite) {
    return context
        .select(
            concat(
                CONTACT.CIVILITE,
                val(" "),
                CONTACT.NOM,
                val(" "),
                CONTACT.PRENOM,
                val(", "),
                CONTACT.FONCTION))
        .from(CONTACT)
        .where(CONTACT.ID_GESTIONNAIRE_SITE.eq(idGestionnaireSite))
        .fetchInto(String.class);
  }

  /**
   * Récupère tous les idContact d'un gestionnaire_site
   *
   * @param idGestionnaireSite
   * @return la liste des identifiants des contacts
   */
  public List<Long> getidContactWithIdGestionnaireSite(Long idGestionnaireSite) {
    return context
        .select(CONTACT.ID)
        .from(CONTACT)
        .where(
            CONTACT.APPARTENANCE.eq(GlobalConstants.GESTIONNAIRE)) // Théoriquement pas nécessaire
        .and(CONTACT.ID_GESTIONNAIRE_SITE.eq(idGestionnaireSite))
        .fetchInto(Long.class);
  }
}
