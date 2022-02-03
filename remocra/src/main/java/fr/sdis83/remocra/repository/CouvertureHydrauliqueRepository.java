package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.model.PlusProchePei;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.COUVERTURE_HYDRAULIQUE_PEI;
import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.COUVERTURE_HYDRAULIQUE_ZONAGE;

@Configuration

public class CouvertureHydrauliqueRepository {

  @Autowired
  DSLContext context;

  @Autowired
  JpaTransactionManager transactionManager;

  @Autowired
  ParamConfService paramConfService;


  @PersistenceContext
  private EntityManager entityManager;

  public CouvertureHydrauliqueRepository() {

  }

  /**
   * Calcul de la couverture hydraulique
   */
  public void calcul(String hydrantsExistants, String hydrantsProjet, String etude, String useReseauImporte) {
    HashMap<String, ArrayList<Integer>> existants = new JSONDeserializer<HashMap<String,ArrayList<Integer>>>().deserialize(hydrantsExistants);
    HashMap<String, ArrayList<Integer>> projets = new JSONDeserializer<HashMap<String,ArrayList<Integer>>>().deserialize(hydrantsProjet);
    Long idEtude = Long.valueOf(etude);
    String reseauImporte = (Boolean.valueOf(useReseauImporte)) ? idEtude.toString() : "NULL";

    String listeHydrants = existants.get("hydrants").toString().replace("[", "").replace("]", "");
    String listeProjets = projets.get("projets").toString().replace("[", "").replace("]", "");

    // Nettoyage des entrées
    this.deleteCouverture(idEtude);

    // Condition pour restreindre les traitements aux PEI souhaités
    StringBuilder condition = new StringBuilder("WHERE FALSE ");
    if(listeHydrants.length() > 0) {
      condition.append("OR (identifiant IN ("+listeHydrants+") AND type = 'HYDRANT')");
    }
    if(listeProjets.length() > 0) {
      condition.append("OR (identifiant IN ("+listeProjets+") AND type = 'PROJET')");
    }

    // Récupération des isodistances
    ArrayList<Integer> distances = new ArrayList<Integer>();
    for(String s : paramConfService.getDeciIsodistances().replaceAll(" ", "").split(",")) {
      distances.add(Integer.parseInt(s));
    }

    // Ajout des jonctions
    entityManager.createNativeQuery("SELECT couverture_hydraulique.inserer_jonction_pei(id, 25, "+reseauImporte+") " +
            "FROM couverture_hydraulique.pei "+condition).getResultList();

    // Parcours du graphe
    entityManager.createNativeQuery("SELECT couverture_hydraulique.parcours_couverture_hydraulique(id, "+idEtude+", "
      +reseauImporte+", array"+distances+") " +
            "FROM couverture_hydraulique.pei " + condition).getResultList();

    // Zonage
    entityManager.createNativeQuery("SELECT couverture_hydraulique.couverture_hydraulique_zonage("+idEtude+", array"+distances+")").getResultList();

    // Retrait des jonctions
    entityManager.createNativeQuery("SELECT couverture_hydraulique.retirer_jonction_pei(r.pei_troncon) " +
            "FROM couverture_hydraulique.reseau r " +
            "WHERE r.pei_troncon IS NOT NULL").getResultList();

  }

  public void deleteCouverture(Long idEtude) {
    context.deleteFrom(COUVERTURE_HYDRAULIQUE_PEI).where(COUVERTURE_HYDRAULIQUE_PEI.ETUDE.eq(idEtude)).execute();
    context.deleteFrom(COUVERTURE_HYDRAULIQUE_ZONAGE).where(COUVERTURE_HYDRAULIQUE_ZONAGE.ETUDE.eq(idEtude)).execute();
  }

  public PlusProchePei closestPei(String json) throws CRSException, IllegalCoordinateException {
    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);
    Double longitude = (obj.get("longitude") != null) ? Double.valueOf(obj.get("longitude").toString()) : null;
    Double latitude = (obj.get("latitude") != null) ? Double.valueOf(obj.get("latitude").toString()) : null;

    Integer distanceMaxParcours = paramConfService.getDeciDistanceMaxParcours();

    Geometry geom = GeometryUtil.createPoint(longitude, latitude, "2154", "2154");
    String query = "SELECT pei, chemin, dist FROM couverture_hydraulique.plus_proche_pei(ST_GeomFromText('"+geom.toText()+"', 2154), "+distanceMaxParcours+", NULL)";

    Object[] result = (Object[]) entityManager.createNativeQuery(query).getSingleResult();

    Long idPei = (result[0] != null && result[0].toString() != null) ? Long.valueOf(result[0].toString()) : null;
    String geometrieChemin = (result[1] != null) ? result[1].toString() : null;
    String distance = (result[2] != null) ? result[2].toString() : null;

    if(idPei != null && idPei > -1) {
      String q = "SELECT ST_AsText(geometrie) " +
                      "FROM couverture_hydraulique.pei WHERE id = "+idPei;
      String wktGeomPei = entityManager.createNativeQuery(q).getSingleResult().toString();
      PlusProchePei p = new PlusProchePei();
      Geometry geomPei = GeometryUtil.toGeometry(wktGeomPei, 2154);

      p.setWktGeometriePei(geomPei.toText());
      p.setWktGeometrieChemin(geometrieChemin);
      p.setDistance(Double.valueOf(distance));
      return p;
    }
    return null;
  }

}
