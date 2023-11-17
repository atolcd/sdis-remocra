package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.GlobalConstants.SRID_2154;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.ZONE_COMPETENCE;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.enums.DestinataireType;
import fr.sdis83.remocra.web.model.DestinataireModel;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DestinataireRepository {

  @Autowired DSLContext context;

  DestinataireRepository(DSLContext context) {
    this.context = context;
  }

  public DestinataireRepository() {}

  @Bean
  public DestinataireRepository destinataireRepository(DSLContext context) {
    return new DestinataireRepository(context);
  }

  /**
   * Récupère tous les id_organisme contenu dans la zone de compétence de l'organisme fourni
   *
   * @param organismeGeometrie
   * @return une liste de long, id_gestionnaire
   */
  public List<Long> getAllOrganismeIdZCByGeometry(Geometry organismeGeometrie) {
    return context
        .select(ORGANISME.ID)
        .from(ORGANISME)
        .join(ZONE_COMPETENCE)
        .on(ORGANISME.ZONE_COMPETENCE.eq(ZONE_COMPETENCE.ID))
        .where(
            "ST_CONTAINS(st_setsrid(ST_GeomFromText({0})," + SRID_2154 + "), {1})",
            organismeGeometrie.toText(),
            ZONE_COMPETENCE.GEOMETRIE)
        .fetchInto(Long.class);
  }

  /**
   * Récupère toutes les informations destinataire des utilisateurs
   *
   * @return une liste de DestinataireModel de type utilisateur
   */
  public List<DestinataireModel> getDestinataireUtilisateur(List<Long> listeIdOrganisme) {
    boolean condition = !listeIdOrganisme.isEmpty();
    return context
        .select(
            UTILISATEUR.ID,
            DSL.concat(UTILISATEUR.NOM, DSL.val(" "), UTILISATEUR.PRENOM).as("nom"),
            UTILISATEUR.EMAIL,
            PROFIL_UTILISATEUR.NOM.as("fonction"),
            DSL.val(DestinataireType.UTILISATEUR.getType()).as("type"))
        .from(UTILISATEUR)
        .join(PROFIL_UTILISATEUR)
        .on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
        .where(condition ? UTILISATEUR.ORGANISME.in(listeIdOrganisme) : DSL.trueCondition())
        .and(UTILISATEUR.EMAIL.isNotNull())
        .and(UTILISATEUR.ACTIF)
        .and(UTILISATEUR.MESSAGE_REMOCRA)
        .fetchInto(DestinataireModel.class);
  }

  /**
   * Récupère toutes les informations destinataire des organismes
   *
   * @return une liste de DestinataireModel de type organismes
   */
  public List<DestinataireModel> getDestinataireOrganisme(List<Long> listeIdOrganisme) {
    boolean condition = !listeIdOrganisme.isEmpty();
    return context
        .select(
            ORGANISME.ID,
            ORGANISME.NOM,
            ORGANISME.EMAIL_CONTACT.as("email"),
            TYPE_ORGANISME.NOM.as("fonction"),
            DSL.val(DestinataireType.ORGANISME.getType()).as("type"))
        .from(ORGANISME)
        .join(TYPE_ORGANISME)
        .on(ORGANISME.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
        .where(condition ? ORGANISME.ID.in(listeIdOrganisme) : DSL.trueCondition())
        .and(ORGANISME.ACTIF)
        .and(ORGANISME.EMAIL_CONTACT.isNotNull())
        .fetchInto(DestinataireModel.class);
  }

  /**
   * Récupère toutes les informations destinataire des contacts de gestionnaires
   *
   * @return une liste de DestinataireModel de type contacts Gestionnaire
   */
  public List<DestinataireModel> getDestinataireContactGestionnaire() {
    return context
        .select(
            CONTACT.ID,
            DSL.concat(CONTACT.NOM, DSL.val(" "), CONTACT.PRENOM).as("nom"),
            CONTACT.EMAIL,
            CONTACT.FONCTION,
            DSL.val(DestinataireType.CONTACT_GESTIONNAIRE.getType()).as("type"))
        .from(CONTACT)
        .join(GESTIONNAIRE)
        .on(CONTACT.ID_APPARTENANCE.eq(GESTIONNAIRE.ID.cast(String.class)))
        .where(CONTACT.APPARTENANCE.eq(GlobalConstants.GESTIONNAIRE))
        .and(GESTIONNAIRE.ACTIF)
        .and(CONTACT.EMAIL.isNotNull())
        .fetchInto(DestinataireModel.class);
  }

  /**
   * Récupère toutes les informations destinataire des contacts d'organismes
   *
   * @return une liste de DestinataireModel de type contacts Organismes
   */
  public List<DestinataireModel> getDestinataireContactOrganisme(List<Long> listeIdOrganisme) {
    boolean condition = !listeIdOrganisme.isEmpty();
    return context
        .select(
            CONTACT.ID,
            DSL.concat(CONTACT.NOM, DSL.val(" "), CONTACT.PRENOM).as("nom"),
            CONTACT.EMAIL,
            CONTACT.FONCTION,
            DSL.val(DestinataireType.CONTACT_ORGANISME.getType()).as("type"))
        .from(CONTACT)
        .join(ORGANISME)
        .on(CONTACT.ID_APPARTENANCE.eq(ORGANISME.ID.cast(String.class)))
        .where(condition ? CONTACT.ID_APPARTENANCE.in(listeIdOrganisme) : DSL.trueCondition())
        .and(CONTACT.APPARTENANCE.eq(GlobalConstants.ORGANISME))
        .and(ORGANISME.ACTIF)
        .and(CONTACT.EMAIL.isNotNull())
        .fetchInto(DestinataireModel.class);
  }
}
