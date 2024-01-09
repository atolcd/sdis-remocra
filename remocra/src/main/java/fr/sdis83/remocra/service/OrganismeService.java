package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class OrganismeService extends AbstractService<Organisme> {

  private final Logger logger = Logger.getLogger(getClass());
  @Autowired private ParametreDataProvider parametreProvider;

  public OrganismeService() {
    super(Organisme.class);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<Organisme> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("query".equals(itemFilter.getFieldName())
        || "nom".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("nom");
      predicat =
          cBuilder.like(cBuilder.upper(cpPath), "%" + itemFilter.getValue().toUpperCase() + "%");
    } else if ("typeOrganisme".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("typeOrganisme").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("typeOrganismeCode".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("typeOrganisme").get("code");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else {
      logger.info(
          "processFilterItem non traité "
              + itemFilter.getFieldName()
              + " ("
              + itemFilter.getValue()
              + ")");
    }
    return predicat;
  }

  @Override
  protected boolean processItemSortings(
      ArrayList<Order> orders,
      ItemSorting itemSorting,
      CriteriaBuilder cBuilder,
      Root<Organisme> from) {
    if ("typeOrganismeId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("typeOrganisme").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("profilOrganismeId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("profilOrganisme").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("zoneCompetenceId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("zoneCompetence").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("organismeParentId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("organismeParent").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else {
      return super.processItemSortings(orders, itemSorting, cBuilder, from);
    }
  }

  /**
   * Désaffecte l'organisme parent de tous les organismes correspondant à un type d'organisme
   * spécifique
   *
   * @param idTypeOrganisme L'ID du type d'organisme spécifique
   */
  @Transactional
  public int removeOrganismeParentForSpecificType(Long idTypeOrganisme) {
    Query query =
        entityManager
            .createNativeQuery(
                ("UPDATE remocra.organisme SET organisme_parent=NULL WHERE type_organisme=(:idTypeOrganisme)"))
            .setParameter("idTypeOrganisme", idTypeOrganisme);
    return query.executeUpdate();
  }

  /**
   * Renvoie le nombre d'organismes enfants pour un organisme donné
   *
   * @param id L'ID de l'organisme parent
   * @return INT le nombre d'organismes enfants
   */
  public int nbOrganismesAvecParentEtProfilSpecifique(Long id) {
    Query query =
        entityManager
            .createNativeQuery(
                "SELECT CAST(COUNT(*) AS INTEGER) FROM remocra.organisme "
                    + "WHERE organisme_parent = :id")
            .setParameter("id", id);
    List<Integer> response = query.getResultList();
    return response.get(0);
  }

  /**
   * Désaffecte les organismes parent d'un organisme spécifique
   *
   * @param idOrganisme L'ID de l'organisme parent
   */
  @Transactional
  public int removeOrganismeParentForSpecificParent(Long idOrganisme) {
    Query query =
        entityManager
            .createNativeQuery(
                ("UPDATE remocra.organisme SET organisme_parent=NULL WHERE organisme_parent=(:idOrganisme)"))
            .setParameter("idOrganisme", idOrganisme);
    return query.executeUpdate();
  }

  /*
   * Retourne la liste des organismes dont la zone de compétence comprend le PEI spécifié
   * @param geometrie La géométrie (de type POINT) du PEI
   * @param organismesAcceptes Une liste contenant le code des type d'organisme souhaités
   * @return Un objet JSON contenant certains attributs des organismes valides
   */
  public JSONArray getAvailableOrganismes(String geometrie, List<String> organismesAcceptes) {
    Query query =
        entityManager
            .createNativeQuery(
                "SELECT o.id, o.nom, t.code "
                    + "FROM remocra.organisme o "
                    + "JOIN remocra.zone_competence zc ON (zc.id = o.zone_competence) "
                    + "JOIN remocra.type_organisme t ON t.id=o.type_organisme "
                    + "WHERE (ST_Intersects(ST_GeomFromText(:geometrie, :srid), zc.geometrie)) AND (o.type_organisme IN "
                    + "(SELECT id FROM remocra.type_organisme WHERE code IN (:typeOrganismes) ))")
            .setParameter("geometrie", geometrie)
            .setParameter("srid", parametreProvider.get().getSridInt())
            .setParameter("typeOrganismes", organismesAcceptes);

    JSONArray json = new JSONArray();
    List<Object[]> organisme = query.getResultList();
    for (Object[] o : organisme) {
      JSONObject obj = new JSONObject();
      obj.put("id", Long.valueOf(o[0].toString()));
      obj.put("nom", o[1].toString());
      obj.put("typeOrganisme", o[2].toString());
      json.add(obj);
    }
    return json;
  }
}
