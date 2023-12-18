package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.COUVERTURE_HYDRAULIQUE_PEI;
import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.COUVERTURE_HYDRAULIQUE_ZONAGE;
import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.PEI;
import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.RESEAU;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.model.PlusProchePei;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class CouvertureHydrauliqueRepository {

  @Autowired DSLContext context;

  @Autowired JpaTransactionManager transactionManager;

  @Autowired ParametreDataProvider parametreProvider;

  @PersistenceContext private EntityManager entityManager;

  private static final String TYPE_HYDRANT = "HYDRANT";
  private static final String TYPE_PROJET = "PROJET";

  public CouvertureHydrauliqueRepository() {}

  public void executeInsererJoinctionPei(
      int distanceMaxAuReseau,
      Long reseauImporte,
      List<Integer> idsHydrant,
      List<Integer> idsHydrantProjet) {

    context
        .select(
            DSL.field(
                "couverture_hydraulique.inserer_jonction_pei("
                    + PEI.ID
                    + ", "
                    + distanceMaxAuReseau
                    + ", "
                    + reseauImporte
                    + ")"))
        .from(PEI)
        .where(
            idsHydrant.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrant).and(PEI.TYPE.eq(TYPE_HYDRANT))
                : DSL.falseCondition())
        .or(
            idsHydrantProjet.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrantProjet).and(PEI.TYPE.eq(TYPE_PROJET))
                : DSL.falseCondition())
        .execute();
  }

  public void executeParcoursCouverture(
      Long reseauImporte,
      Long idEtude,
      List<Integer> distances,
      List<Integer> idsHydrant,
      List<Integer> idsHydrantProjet,
      int profondeurCouverture) {
    context
        .select(
            DSL.field(
                "couverture_hydraulique.parcours_couverture_hydraulique("
                    + PEI.ID
                    + ", "
                    + idEtude
                    + ", "
                    + reseauImporte
                    + ", array"
                    + distances
                    + ", "
                    + profondeurCouverture
                    + ")"))
        .from(PEI)
        .where(
            idsHydrant.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrant).and(PEI.TYPE.eq(TYPE_HYDRANT))
                : DSL.falseCondition())
        .or(
            idsHydrantProjet.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrantProjet).and(PEI.TYPE.eq(TYPE_PROJET))
                : DSL.falseCondition())
        .execute();
  }

  public void executeCouvertureHydrauliqueZonage(
      Long idEtude,
      List<Integer> distances,
      List<Integer> idsHydrant,
      List<Integer> idsHydrantProjet,
      int profondeurCouverture) {

    context
        .select(
            DSL.field(
                "couverture_hydraulique.couverture_hydraulique_zonage("
                    + idEtude
                    + ", array"
                    + distances
                    + ", "
                    + profondeurCouverture
                    + ")"))
        .from(PEI)
        .where(
            idsHydrant.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrant).and(PEI.TYPE.eq(TYPE_HYDRANT))
                : DSL.falseCondition())
        .or(
            idsHydrantProjet.size() > 0
                ? PEI.IDENTIFIANT.in(idsHydrantProjet).and(PEI.TYPE.eq(TYPE_PROJET))
                : DSL.falseCondition())
        .execute();
  }

  public void executeRetirerJonctionPei() {
    context
        .select(
            DSL.field("couverture_hydraulique.retirer_jonction_pei(" + RESEAU.PEI_TRONCON + ")"))
        .from(RESEAU)
        .where(RESEAU.PEI_TRONCON.isNotNull())
        .execute();
  }

  public void deleteCouverture(Long idEtude) {
    context
        .deleteFrom(COUVERTURE_HYDRAULIQUE_PEI)
        .where(COUVERTURE_HYDRAULIQUE_PEI.ETUDE.eq(idEtude))
        .execute();
    context
        .deleteFrom(COUVERTURE_HYDRAULIQUE_ZONAGE)
        .where(COUVERTURE_HYDRAULIQUE_ZONAGE.ETUDE.eq(idEtude))
        .execute();
  }

  public PlusProchePei closestPei(String json) throws CRSException, IllegalCoordinateException {
    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);
    Double longitude =
        (obj.get("longitude") != null) ? Double.valueOf(obj.get("longitude").toString()) : null;
    Double latitude =
        (obj.get("latitude") != null) ? Double.valueOf(obj.get("latitude").toString()) : null;

    Integer distanceMaxParcours = parametreProvider.get().getDeciDistanceMaxParcours();

    Geometry geom =
        GeometryUtil.createPoint(
            longitude,
            latitude,
            GlobalConstants.SRID_PARAM.toString(),
            GlobalConstants.SRID_PARAM.toString());
    String query =
        "SELECT pei, chemin, dist FROM couverture_hydraulique.plus_proche_pei(ST_GeomFromText('"
            + geom.toText()
            + "', "
            + GlobalConstants.SRID_PARAM
            + "), "
            + distanceMaxParcours
            + ", NULL)";

    Object[] result = (Object[]) entityManager.createNativeQuery(query).getSingleResult();

    Long idPei =
        (result[0] != null && result[0].toString() != null)
            ? Long.valueOf(result[0].toString())
            : null;
    String geometrieChemin = (result[1] != null) ? result[1].toString() : null;
    String distance = (result[2] != null) ? result[2].toString() : null;

    if (idPei != null && idPei > -1) {
      String q =
          "SELECT ST_AsText(geometrie) " + "FROM couverture_hydraulique.pei WHERE id = " + idPei;
      String wktGeomPei = entityManager.createNativeQuery(q).getSingleResult().toString();
      PlusProchePei p = new PlusProchePei();
      Geometry geomPei = GeometryUtil.toGeometry(wktGeomPei, GlobalConstants.SRID_PARAM);

      p.setWktGeometriePei(geomPei.toText());
      p.setWktGeometrieChemin(geometrieChemin);
      p.setDistance(Double.valueOf(distance));
      return p;
    }
    return null;
  }
}
