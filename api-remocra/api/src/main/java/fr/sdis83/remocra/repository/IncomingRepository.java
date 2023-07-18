package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.jooq.impl.DSL;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.UUID;

import static fr.sdis83.remocra.db.model.incoming.Tables.NEW_HYDRANT;
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
}
