package fr.sdis83.remocra.service;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class TourneeService extends AbstractService<Tournee> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private UtilisateurService utilisateurService;

  public TourneeService() {
    super(Tournee.class);
  }

  @Bean
  public TourneeService tourneeService() {
    return new TourneeService();
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<Tournee> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("query"
        .equals(
            itemFilter
                .getFieldName())) { // Recherche sur le nom de la tournée OU le nom de l'organisme
      Expression<String> cpPath = from.get("id");
      String sanitizedValue = itemFilter.getValue().replaceAll("'", "''").replaceAll("\"", "\\\"");
      String sql =
          "SELECT t.id from remocra.tournee t "
              + "LEFT JOIN remocra.organisme o ON o.id = t.affectation "
              + "WHERE UPPER(t.nom) LIKE UPPER('%"
              + sanitizedValue
              + "%') OR UPPER(o.nom) LIKE UPPER('%"
              + sanitizedValue
              + "%');";

      Query query = entityManager.createNativeQuery(sql);
      List<BigInteger> idTournees = query.getResultList();

      if (idTournees.size() > 0) {
        predicat = cBuilder.isTrue(cpPath.in(idTournees));
      } else {
        predicat = cBuilder.equal(from.get("id"), -1);
      }
    } else if ("nom".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("nom");
      predicat =
          cBuilder.like(
              cBuilder.concat("", cBuilder.upper(cpPath)),
              "%" + itemFilter.getValue().toUpperCase() + "%");
    } else if ("affectation".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("affectation").get("id");
      if (isNumeric(itemFilter.getValue())) {
        // Si numérique, filtre sur organisme + organismes enfants
        ArrayList<Integer> ids =
            Organisme.getOrganismeAndChildren(Integer.valueOf(itemFilter.getValue()));

        // Ajout d'un id négatif car cette version d'hibernate gère mal le cas où le tableau est
        // vide avec le IN
        ids.add(-1);
        predicat = cBuilder.isTrue(cpPath.in(ids));
      } else {
        // Si texte, filtre sur organisme + organismes enfants de k'utilisateur courant + nom
        Long userOrganismeId = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
        ArrayList<Integer> ids = Organisme.getOrganismeAndChildren(userOrganismeId.intValue());

        String sql =
            "SELECT id FROM remocra.organisme WHERE id IN (:ids) AND UPPER(nom) LIKE UPPER('%"
                + itemFilter.getValue()
                + "%')";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("ids", ids);
        List<BigInteger> idOrganismes = query.getResultList();

        // Ajout d'un id négatif car cette version d'hibernate gère mal le cas où le tableau est
        // vide avec le IN
        idOrganismes.add(new BigInteger("-1"));

        predicat = cBuilder.isTrue(cpPath.in(idOrganismes));
      }

    } else if ("hydrantCount".equals(itemFilter.getFieldName())) {
      Expression<Collection<String>> hydrants = from.get("hydrants");
      predicat = cBuilder.equal(cBuilder.size(hydrants), itemFilter.getValue());
    } else if ("reserved".equals(itemFilter.getFieldName())) {
      boolean reserved = Boolean.parseBoolean(itemFilter.getValue());
      Expression<String> cpPath = from.get("reservation");
      if (reserved) {
        predicat = cpPath.isNotNull();
      } else {
        predicat = cpPath.isNull();
      }
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
      Root<Tournee> from) {
    if ("affectation".equals(itemSorting.getFieldName())) {
      Path<String> nom = from.get("affectation").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(nom) : cBuilder.asc(nom));
    } else if ("reservation".equals(itemSorting.getFieldName())) {
      Path<String> nom = from.join("reservation", JoinType.LEFT).get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(nom) : cBuilder.asc(nom));
    }
    return super.processItemSortings(orders, itemSorting, cBuilder, from);
  }

  public List<Tournee> getTourneeAll(Utilisateur utilisateur) {
    String sql = "SELECT t FROM Tournee t where t.affectation = :organisme";
    TypedQuery<Tournee> query = entityManager.createQuery(sql, Tournee.class);
    query.setParameter("organisme", utilisateur.getOrganisme());
    return query.getResultList();
  }

  public List<Tournee> getTourneeDisponible(Utilisateur utilisateur) {
    String sql =
        "SELECT t FROM Tournee t where t.affectation = :organisme and t.reservation is null and t.etat < 100 and t.hydrantCount > 0";
    TypedQuery<Tournee> query = entityManager.createQuery(sql, Tournee.class);
    query.setParameter("organisme", utilisateur.getOrganisme());
    return query.getResultList();
  }

  @Transactional
  public List<Hydrant> getHydrants(Long id) {
    List<Hydrant> hydrants = new ArrayList<Hydrant>();
    String sql = "SELECT hydrant FROM remocra.hydrant_tournees ht where ht.tournees =:id";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", id);
    List<BigInteger> idHydrants = query.getResultList();
    for (int i = 0; i < idHydrants.size(); i++) {
      hydrants.add(Hydrant.findHydrant(idHydrants.get(i).longValue()));
    }
    return hydrants;
  }

  @Transactional
  public List<String> getNumHydrants(Long id) {
    String sql =
        "SELECT numero FROM remocra.hydrant h where h.id in (select hydrant from remocra.hydrant_tournees ht where ht.tournees =:id)";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", id);
    List<String> numHydrants = query.getResultList();
    return numHydrants;
  }

  @Transactional
  public String getNomTournee(Hydrant h, Long organisme) {
    String nom = null;
    String sql =
        "SELECT COALESCE(string_agg(nom, ', '), '') FROM remocra.tournee t where t.affectation in(:organismes) and "
            + "t.id in (SELECT tournees FROM remocra.hydrant_tournees ht where ht.hydrant =:id) ";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", h.getId());
    query.setParameter("organismes", Organisme.getOrganismeAndChildren(organisme.intValue()));
    if (query.getResultList().size() != 0) {
      nom = String.valueOf(query.getResultList().get(0));
    }
    return nom;
  }
}
