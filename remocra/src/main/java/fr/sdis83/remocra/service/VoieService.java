package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.Voie;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoieService {

  @Autowired private ParamConfService paramConfService;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<Voie> findVoiesByMotClassantOrNomLike(
      int firstResult,
      int maxResults,
      List<ItemSorting> itemSortings,
      List<ItemFilter> itemFilters) {

    EntityManager em = Voie.entityManager();
    CriteriaBuilder cBuilder = em.getCriteriaBuilder();
    CriteriaQuery<Voie> itemQuery = cBuilder.createQuery(Voie.class);
    Root<Voie> from = itemQuery.from(Voie.class);

    Map<String, Object> parameters = new HashMap<String, Object>();

    // Filtre spatial
    ItemFilter wktFilter = ItemFilter.getFilter(itemFilters, "wkt");

    // Filtres
    Predicate[] filterPredicates = makeFilterPredicates(parameters, from, itemFilters);

    // Tris
    List<Order> orders = null;

    ParameterExpression<Geometry> wktOrderParam = null;
    if (wktFilter != null) {
      // Si filtre spatial, tri ascendant selon les distances
      orders = new ArrayList<Order>();
      Path<String> geometrie = from.get("geometrie");
      wktOrderParam = cBuilder.parameter(Geometry.class);
      orders.add(
          cBuilder.asc(cBuilder.function("st_distance", Double.class, geometrie, wktOrderParam)));

    } else {
      // Cas classique de tri
      orders = makeOrders(from, itemSortings);
    }

    itemQuery.select(from).orderBy(orders);
    itemQuery.where(filterPredicates);

    TypedQuery<Voie> itemTypedQuery = em.createQuery(itemQuery);

    // Valeur pour les paramètres (si besoin)
    if (wktFilter != null) {
      String wktValue = "SRID=" + GlobalConstants.SRID_2154 + ";" + wktFilter.getValue();
      itemTypedQuery.setParameter((Parameter) wktOrderParam, wktValue);
      itemTypedQuery.setParameter((Parameter) parameters.get("WKT_PARAM"), wktValue);
      itemTypedQuery.setParameter(
          (Parameter) parameters.get("DIST_PARAM"), paramConfService.getToleranceVoiesMetres());
    }

    return itemTypedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
  }

  public Predicate[] makeFilterPredicates(
      Map<String, Object> parameters, Root<Voie> from, List<ItemFilter> itemFilters) {

    List<Predicate> predicateList = new ArrayList<Predicate>();
    if (itemFilters != null && !itemFilters.isEmpty()) {
      for (ItemFilter itemFilter : itemFilters) {
        if ("communeId".equals(itemFilter.getFieldName())) {
          predicateList.add(communeFieldPredicate(from, itemFilter));
        } else if ("mc".equals(itemFilter.getFieldName())) {
          if (itemFilter.getValue() != null) {
            predicateList.add(mcOrNomFieldPredicate(from, itemFilter));
          }
        } else if ("wkt".equals(itemFilter.getFieldName())) {
          if (itemFilter.getValue() != null) {
            predicateList.add(wktFieldPredicate(parameters, from, itemFilter));
          }
        }
      }
    }

    Predicate[] predicates = new Predicate[0];
    predicates = predicateList.toArray(predicates);

    return predicates;
  }

  private Predicate communeFieldPredicate(Root<Voie> from, ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = Voie.entityManager().getCriteriaBuilder();
    Path<String> commune = from.get("commune").get("id");
    Predicate equal = cBuilder.equal(commune, itemFilter.getValue());
    return equal;
  }

  private Predicate mcOrNomFieldPredicate(Root<Voie> from, ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = Voie.entityManager().getCriteriaBuilder();
    String nom = itemFilter.getValue();
    if (nom == null) {
      return null;
    }
    Path<String> motClassant = from.get("motClassant");
    Path<String> source = from.get("source");
    Path<String> nomPath = from.get("nom");
    Predicate isRouteP = cBuilder.equal(source, Voie.Source.ROUTE);
    Predicate motClassantP =
        cBuilder.like(cBuilder.lower(motClassant), "%" + nom.toLowerCase() + "%");
    Predicate routeEtMotClassant = cBuilder.and(isRouteP, motClassantP);
    Predicate isPisteP = cBuilder.equal(source, Voie.Source.PISTE);
    Predicate nomP = cBuilder.like(cBuilder.lower(nomPath), "%" + nom.toLowerCase() + "%");
    Predicate pisteEtNom = cBuilder.and(isPisteP, nomP);
    return cBuilder.or(routeEtMotClassant, pisteEtNom);
  }

  private Predicate wktFieldPredicate(
      Map<String, Object> parameters, Root<Voie> from, ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = Voie.entityManager().getCriteriaBuilder();

    Path<String> geometrie = from.get("geometrie");

    // Pour les appels à des fonctions, on remonte les instances des
    // Expressions car on en a besoin pour définir les valeurs au niveau de
    // la requête.
    ParameterExpression<Geometry> wktParam = cBuilder.parameter(Geometry.class);
    parameters.put("WKT_PARAM", wktParam);
    ParameterExpression<Integer> distParam = cBuilder.parameter(Integer.class);
    parameters.put("DIST_PARAM", distParam);
    Predicate within =
        cBuilder.isTrue(
            cBuilder.function("st_dwithin", Boolean.class, geometrie, wktParam, distParam));

    return within;
  }

  public List<Order> makeOrders(Root<Voie> from, List<ItemSorting> itemSortings) {
    ArrayList<Order> orders = new ArrayList<Order>();
    CriteriaBuilder cBuilder = Voie.entityManager().getCriteriaBuilder();
    if (itemSortings != null && !itemSortings.isEmpty()) {
      for (ItemSorting itemSorting : itemSortings) {
        orders.add(
            "DESC".equals(itemSorting.getDirection())
                ? cBuilder.desc(from.get(itemSorting.getFieldName()))
                : cBuilder.asc(from.get(itemSorting.getFieldName())));
      }
    }
    return orders;
  }
}
