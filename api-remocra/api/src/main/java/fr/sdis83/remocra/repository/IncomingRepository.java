package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.jooq.impl.DSL;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.referentiel.ContactRoleModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.UUID;

import static fr.sdis83.remocra.db.model.incoming.Tables.NEW_HYDRANT;
import static fr.sdis83.remocra.db.model.incoming.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.incoming.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.incoming.Tables.CONTACT_ROLE;
import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.VOIE;

public class IncomingRepository {
    private final DSLContext context;
    private final TransactionManager transactionManager;

    @Inject
    public IncomingRepository(DSLContext context, TransactionManager transactionManager) {
        this.context = context;
        this.transactionManager = transactionManager;
    }


    /**
     * Crée un hydrant
     */
    public int insertPei(
            UUID idHydrant,
            UUID idGestionnaire,
            Long idCommune,
            Long idNature,
            Long idNatureDeci,
            String code,
            String observations,
            Geometry geometrie,
            String nomVoie) {
        return transactionManager.transactionResult(() ->
                context.insertInto(NEW_HYDRANT)
                        .set(NEW_HYDRANT.ID_NEW_HYDRANT, idHydrant)
                        .set(NEW_HYDRANT.GEOMETRIE,
                                (Object) DSL.field("st_transform(st_setsrid(ST_GeomFromText('" + geometrie.toText() + "'), 4326), 2154)"))
                        .set(NEW_HYDRANT.CODE_NEW_HYDRANT, code)
                        .set(NEW_HYDRANT.ID_COMMUNE, idCommune)
                        .set(NEW_HYDRANT.VOIE_NEW_HYDRANT, nomVoie)
                        .set(NEW_HYDRANT.ID_NATUREDECI, idNatureDeci)
                        .set(NEW_HYDRANT.ID_NATURE, idNature)
                        .set(NEW_HYDRANT.ID_GESTIONNAIRE, idGestionnaire)
                        .set(NEW_HYDRANT.OBSERVATION_NEW_HYDRANT, observations)
                        .onConflictDoNothing()
                        .execute()
        );
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
            geometry.setSRID(2154);
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
        return context.select(COMMUNE.ID)
                .from(COMMUNE)
                .where("ST_CONTAINS({0}, st_transform(st_setsrid(ST_GeomFromText({1}), 4326), 2154))",
                        COMMUNE.GEOMETRIE,
                        geometrie.toText())
                .fetchOneInto(Long.class);
    }

    public String getVoie(Geometry geometrie) {
        return context.select(VOIE.NOM)
                .from(VOIE)
                .where("ST_CONTAINS({0}, st_transform(st_setsrid(ST_GeomFromText({1}), 4326), 2154))",
                        VOIE.GEOMETRIE,
                        geometrie.toText())
                .fetchOneInto(String.class);
    }

    public int insertGestionnaire(Long idRemocra, String codeGestionnaire, String nomGestionnaire, UUID idGestionnaire) {
        return transactionManager.transactionResult(() ->
                context.insertInto(GESTIONNAIRE)
                        .set(GESTIONNAIRE.ID_GESTIONNAIRE, idGestionnaire)
                        .set(GESTIONNAIRE.ID_GESTIONNAIRE_REMOCRA, idRemocra)
                        .set(GESTIONNAIRE.CODE_GESTIONNAIRE, codeGestionnaire)
                        .set(GESTIONNAIRE.NOM_GESTIONNAIRE, nomGestionnaire)
                        .execute()
        );
    }

    public boolean checkGestionnaireExist(UUID idGestionnaire) {
        return context.fetchExists(
                context.selectFrom(GESTIONNAIRE)
                        .where(GESTIONNAIRE.ID_GESTIONNAIRE.eq(idGestionnaire))
        );
    }

    public int insertContact(ContactModel contactModel, UUID idContact, UUID idGestionnaire) {
        return transactionManager.transactionResult(() ->
                context.insertInto(CONTACT)
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
                        .set(CONTACT.PAYS_CONTACT, contactModel.getPays())
                        .set(CONTACT.EMAIL_CONTACT, contactModel.getEmail())
                        .set(CONTACT.TELEPHONE_CONTACT, contactModel.getTelephone())
                        .onConflictDoNothing()
                        .execute()
        );
    }

    public boolean checkContactExist(UUID idContact) {
        return context.fetchExists(
                context.selectFrom(CONTACT)
                        .where(CONTACT.ID_CONTACT.eq(idContact))
        );
    }

    public int insertContactRole(UUID idContact, Long idRole) {
        return transactionManager.transactionResult(() ->
                context.insertInto(CONTACT_ROLE)
                        .set(CONTACT_ROLE.ID_CONTACT, idContact)
                        .set(CONTACT_ROLE.ID_ROLE, idRole)
                        .onConflictDoNothing()
                        .execute()
        );
    }
}
