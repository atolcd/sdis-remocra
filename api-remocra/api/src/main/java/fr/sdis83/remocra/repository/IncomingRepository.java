package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.incoming.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.incoming.Tables.CONTACT_ROLE;
import static fr.sdis83.remocra.db.model.incoming.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.incoming.Tables.HYDRANT_PHOTO;
import static fr.sdis83.remocra.db.model.incoming.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.incoming.Tables.HYDRANT_VISITE_ANOMALIE;
import static fr.sdis83.remocra.db.model.incoming.Tables.NEW_HYDRANT;
import static fr.sdis83.remocra.db.model.incoming.Tables.TOURNEE;
import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.VOIE;
import static fr.sdis83.remocra.util.GlobalConstants.SRID_4326;
import static fr.sdis83.remocra.util.GlobalConstants.SRID_PARAM;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Contact;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.ContactRole;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Gestionnaire;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantPhoto;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisiteAnomalie;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.NewHydrant;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Tournee;
import fr.sdis83.remocra.web.model.mobilemodel.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.mobilemodel.TourneeModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class IncomingRepository {
  private final DSLContext context;
  private final TransactionManager transactionManager;

  @Inject
  public IncomingRepository(DSLContext context, TransactionManager transactionManager) {
    this.context = context;
    this.transactionManager = transactionManager;
  }

  /** Crée un hydrant */
  public int insertPei(
      UUID idHydrant,
      UUID idGestionnaire,
      Long idGestionnaireRemocra,
      Long idCommune,
      Long idNature,
      Long idNatureDeci,
      String code,
      String observations,
      Geometry geometrie,
      String nomVoie) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(NEW_HYDRANT)
                .set(NEW_HYDRANT.ID_NEW_HYDRANT, idHydrant)
                .set(
                    NEW_HYDRANT.GEOMETRIE,
                    (Object)
                        DSL.field(
                            "st_transform(st_setsrid(ST_GeomFromText('"
                                + geometrie.toText()
                                + "'), "
                                + SRID_4326
                                + "), "
                                + SRID_PARAM
                                + ")"))
                .set(NEW_HYDRANT.CODE_NEW_HYDRANT, code)
                .set(NEW_HYDRANT.ID_COMMUNE, idCommune)
                .set(NEW_HYDRANT.VOIE_NEW_HYDRANT, nomVoie)
                .set(NEW_HYDRANT.ID_NATUREDECI, idNatureDeci)
                .set(NEW_HYDRANT.ID_NATURE, idNature)
                .set(NEW_HYDRANT.ID_GESTIONNAIRE, idGestionnaire)
                .set(NEW_HYDRANT.ID_GESTIONNAIRE_REMOCRA, idGestionnaireRemocra)
                .set(NEW_HYDRANT.OBSERVATION_NEW_HYDRANT, observations)
                .onConflictDoNothing()
                .execute());
  }

  /**
   * Permet de récupérer la géométrie du point d'eau
   *
   * @param longitude
   * @param latitude
   * @return
   */
  public Geometry getGeometrieWithCoordonnnees(Double longitude, Double latitude) {
    String wkt = "POINT(" + longitude + " " + latitude + ")";
    WKTReader fromText = new WKTReader();
    Geometry geometry = null;
    try {
      geometry = fromText.read(wkt);
      geometry.setSRID(SRID_PARAM);
    } catch (ParseException e) {
      throw new RuntimeException("Not a WKT string:" + wkt);
    }
    return geometry;
  }

  /**
   * Permet de récupérer l'id de la commune en fonction de la géométrie de l'hydrant
   *
   * @param geometrie
   * @return
   */
  public Long getCommuneWithGeometrie(Geometry geometrie) {
    return context
        .select(COMMUNE.ID)
        .from(COMMUNE)
        .where(
            "ST_CONTAINS({0}, st_transform(st_setsrid(ST_GeomFromText({1}), "
                + SRID_4326
                + "), "
                + SRID_PARAM
                + "))",
            COMMUNE.GEOMETRIE,
            geometrie.toText())
        .fetchOneInto(Long.class);
  }

  public String getVoie(Geometry geometrie) {
    return context
        .select(VOIE.NOM)
        .from(VOIE)
        .where(
            "ST_CONTAINS({0}, st_transform(st_setsrid(ST_GeomFromText({1}), "
                + SRID_4326
                + "), "
                + SRID_PARAM
                + "))",
            VOIE.GEOMETRIE,
            geometrie.toText())
        .fetchOneInto(String.class);
  }

  public int insertGestionnaire(
      Long idRemocra, String codeGestionnaire, String nomGestionnaire, UUID idGestionnaire) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(GESTIONNAIRE)
                .set(GESTIONNAIRE.ID_GESTIONNAIRE, idGestionnaire)
                .set(GESTIONNAIRE.ID_GESTIONNAIRE_REMOCRA, idRemocra)
                .set(GESTIONNAIRE.CODE_GESTIONNAIRE, codeGestionnaire)
                .set(GESTIONNAIRE.NOM_GESTIONNAIRE, nomGestionnaire)
                .onConflictDoNothing()
                .execute());
  }

  public boolean checkGestionnaireExist(UUID idGestionnaire) {
    return context.fetchExists(
        context.selectFrom(GESTIONNAIRE).where(GESTIONNAIRE.ID_GESTIONNAIRE.eq(idGestionnaire)));
  }

  public int insertContact(ContactModel contactModel, UUID idContact, UUID idGestionnaire) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(CONTACT)
                .set(CONTACT.ID_CONTACT, idContact)
                .set(CONTACT.ID_CONTACT_REMOCRA, contactModel.getIdRemocra())
                .set(CONTACT.ID_GESTIONNAIRE, idGestionnaire)
                .set(CONTACT.FONCTION_CONTACT, contactModel.getFonction())
                .set(CONTACT.CIVILITE_CONTACT, contactModel.getCivilite())
                .set(CONTACT.NOM_CONTACT, contactModel.getNom())
                .set(CONTACT.PRENOM_CONTACT, contactModel.getPrenom())
                .set(CONTACT.CODE_POSTAL_CONTACT, contactModel.getCodePostal())
                .set(CONTACT.VILLE_CONTACT, contactModel.getVille())
                .set(CONTACT.NUMERO_VOIE_CONTACT, contactModel.getNumeroVoie())
                .set(CONTACT.SUFFIXE_VOIE_CONTACT, contactModel.getSuffixeVoie())
                .set(CONTACT.VOIE_CONTACT, contactModel.getVoie())
                .set(CONTACT.LIEU_DIT_CONTACT, contactModel.getLieuDit())
                .set(CONTACT.PAYS_CONTACT, contactModel.getPays())
                .set(CONTACT.EMAIL_CONTACT, contactModel.getEmail())
                .set(CONTACT.TELEPHONE_CONTACT, contactModel.getTelephone())
                .onConflictDoNothing()
                .execute());
  }

  public boolean checkContactExist(UUID idContact) {
    return context.fetchExists(context.selectFrom(CONTACT).where(CONTACT.ID_CONTACT.eq(idContact)));
  }

  public int insertContactRole(UUID idContact, Long idRole) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(CONTACT_ROLE)
                .set(CONTACT_ROLE.ID_CONTACT, idContact)
                .set(CONTACT_ROLE.ID_ROLE, idRole)
                .onConflictDoNothing()
                .execute());
  }

  public int insertVisite(HydrantVisiteModel hydrantVisiteModel, Boolean hasAnomalieChanges) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(HYDRANT_VISITE)
                .set(HYDRANT_VISITE.ID_HYDRANT_VISITE, hydrantVisiteModel.idHydrantVisite())
                .set(HYDRANT_VISITE.ID_HYDRANT, hydrantVisiteModel.idHydrant())
                .set(HYDRANT_VISITE.AGENT1_HYDRANT_VISITE, hydrantVisiteModel.agent1())
                .set(HYDRANT_VISITE.AGENT2_HYDRANT_VISITE, hydrantVisiteModel.agent2())
                .set(HYDRANT_VISITE.CTRL_DEBIT_PRESSION, hydrantVisiteModel.ctrDebitPression())
                .set(HYDRANT_VISITE.DATE_HYDRANT_VISITE, hydrantVisiteModel.date())
                .set(HYDRANT_VISITE.DEBIT_HYDRANT_VISITE, hydrantVisiteModel.debit())
                .set(HYDRANT_VISITE.PRESSION_HYDRANT_VISITE, hydrantVisiteModel.pression())
                .set(HYDRANT_VISITE.PRESSION_DYN_HYDRANT_VISITE, hydrantVisiteModel.pressionDyn())
                .set(HYDRANT_VISITE.OBSERVATIONS_HYDRANT_VISITE, hydrantVisiteModel.observations())
                .set(HYDRANT_VISITE.ID_TYPE_HYDRANT_SAISIE, hydrantVisiteModel.idTypeVisite())
                .set(HYDRANT_VISITE.HAS_ANOMALIE_CHANGES, hasAnomalieChanges)
                .onConflictDoNothing()
                .execute());
  }

  public boolean checkHydrantVisiteExist(UUID idHydrantVisite) {
    return context.fetchExists(
        context
            .selectFrom(HYDRANT_VISITE)
            .where(HYDRANT_VISITE.ID_HYDRANT_VISITE.eq(idHydrantVisite)));
  }

  public int insertHydrantVisiteAnomalie(UUID idHydrantVisite, Long idAnomalie) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(HYDRANT_VISITE_ANOMALIE)
                .set(HYDRANT_VISITE_ANOMALIE.ID_HYDRANT_VISITE, idHydrantVisite)
                .set(HYDRANT_VISITE_ANOMALIE.ID_ANOMALIE, idAnomalie)
                .onConflictDoNothing()
                .execute());
  }

  public int insertTournee(TourneeModel tourneeModel) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(TOURNEE)
                .set(TOURNEE.ID_TOURNEE_REMOCRA, tourneeModel.idRemocra())
                .set(TOURNEE.NOM_TOURNEE, tourneeModel.nom())
                .onConflictDoNothing()
                .execute());
  }

  public Tournee getIncomingTourneeById(Long idTournee) {
    return context
        .selectFrom(TOURNEE)
        .where(TOURNEE.ID_TOURNEE_REMOCRA.eq(idTournee))
        .fetchOneInto(Tournee.class);
  }

  public List<Tournee> getTournees() {
    return context.selectFrom(TOURNEE).fetchInto(Tournee.class);
  }

  public List<NewHydrant> getIncomingNewHydrant() {
    return context.selectFrom(NEW_HYDRANT).fetchInto(NewHydrant.class);
  }

  public void deleteNewHydrant(UUID idNewHydrant) {
    context.deleteFrom(NEW_HYDRANT).where(NEW_HYDRANT.ID_NEW_HYDRANT.eq(idNewHydrant)).execute();
  }

  public List<HydrantVisite> getHydrantVisite(List<Long> listIdHydrant) {
    return context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.ID_HYDRANT.in(listIdHydrant))
        .fetchInto(HydrantVisite.class);
  }

  public List<HydrantVisiteAnomalie> getHydrantVisiteAnomalie(List<UUID> listIdHydrantVisite) {
    return context
        .selectFrom(HYDRANT_VISITE_ANOMALIE)
        .where(HYDRANT_VISITE_ANOMALIE.ID_HYDRANT_VISITE.in(listIdHydrantVisite))
        .fetchInto(HydrantVisiteAnomalie.class);
  }

  public void updateIdRemocraGestionnaire(Long idRemocra, UUID idGestionnaire) {
    context
        .update(GESTIONNAIRE)
        .set(GESTIONNAIRE.ID_GESTIONNAIRE_REMOCRA, idRemocra)
        .where(GESTIONNAIRE.ID_GESTIONNAIRE.eq(idGestionnaire))
        .execute();
  }

  public int insertHydrantPhoto(Long idHydrant, String path, Instant moment, String nomPhoto) {
    return transactionManager.transactionResult(
        () ->
            context
                .insertInto(HYDRANT_PHOTO)
                .set(HYDRANT_PHOTO.ID_HYDRANT_PHOTO, UUID.randomUUID())
                .set(HYDRANT_PHOTO.ID_HYDRANT_HYDRANT_PHOTO, idHydrant)
                .set(HYDRANT_PHOTO.PATH_HYDRANT_PHOTO, path)
                .set(HYDRANT_PHOTO.DATE_HYDRANT_PHOTO, moment)
                .set(HYDRANT_PHOTO.NOM_HYDRANT_PHOTO, nomPhoto)
                .onConflictDoNothing()
                .execute());
  }

  public boolean checkExist(Long idHydrant, String path) {
    return context.fetchExists(
        context
            .selectFrom(HYDRANT_PHOTO)
            .where(HYDRANT_PHOTO.ID_HYDRANT_HYDRANT_PHOTO.eq(idHydrant))
            .and(HYDRANT_PHOTO.PATH_HYDRANT_PHOTO.eq(path)));
  }

  public List<HydrantPhoto> getListHydrantPhoto() {
    return context.selectFrom(HYDRANT_PHOTO).fetchInto(HydrantPhoto.class);
  }

  public List<Gestionnaire> getGestionnaires() {
    return context.selectFrom(GESTIONNAIRE).fetchInto(Gestionnaire.class);
  }

  public List<Contact> getContacts() {
    return context.selectFrom(CONTACT).fetchInto(Contact.class);
  }

  public List<ContactRole> getContactsRoles() {
    return context.selectFrom(CONTACT_ROLE).fetchInto(ContactRole.class);
  }

  public void deleteGestionnaire() {
    context.deleteFrom(GESTIONNAIRE).execute();
  }

  public void deleteContact() {
    context.deleteFrom(CONTACT).execute();
  }

  public void deleteContactRole() {
    context.deleteFrom(CONTACT_ROLE).execute();
  }

  public void deleteTournee() {
    context.deleteFrom(TOURNEE).execute();
  }

  public void deleteHydrantVisiteAnomalie() {
    context.deleteFrom(HYDRANT_VISITE_ANOMALIE).execute();
  }

  public void deleteHydrantVisite() {
    context.deleteFrom(HYDRANT_VISITE).execute();
  }

  public void deleteHydrantPhoto() {
    context.deleteFrom(HYDRANT_PHOTO).execute();
  }
}
