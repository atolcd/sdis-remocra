package fr.sdis83.remocra.repository;

import flexjson.JSONDeserializer;
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

    // Ajout des jonctions
    entityManager.createNativeQuery("SELECT couverture_hydraulique.inserer_jonction_pei(id, 25, "+reseauImporte+") " +
            "FROM couverture_hydraulique.pei "+condition).getResultList();

    // Parcours du graphe
    entityManager.createNativeQuery("SELECT couverture_hydraulique.parcours_couverture_hydraulique(id, "+idEtude+", "+reseauImporte+") " +
            "FROM couverture_hydraulique.pei " + condition).getResultList();

    // Zonage
    entityManager.createNativeQuery("SELECT couverture_hydraulique.couverture_hydraulique_zonage("+idEtude+")").getResultList();

    // Retrait des jonctions
    entityManager.createNativeQuery("SELECT couverture_hydraulique.retirer_jonction_pei(r.pei_troncon) " +
            "FROM couverture_hydraulique.reseau r " +
            "WHERE r.pei_troncon IS NOT NULL").getResultList();

  }

  public void deleteCouverture(Long idEtude) {
    context.deleteFrom(COUVERTURE_HYDRAULIQUE_PEI).where(COUVERTURE_HYDRAULIQUE_PEI.ETUDE.eq(idEtude)).execute();
    context.deleteFrom(COUVERTURE_HYDRAULIQUE_ZONAGE).where(COUVERTURE_HYDRAULIQUE_ZONAGE.ETUDE.eq(idEtude)).execute();
  }

}
