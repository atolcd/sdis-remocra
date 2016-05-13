package fr.sdis83.remocra.domain.remocra;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

privileged aspect Commune_Remocra_SortFilter {

    private static final Logger log = Logger.getLogger(Commune.class);

    public static List<Commune> Commune.findCommunesByNomLike(int firstResult, int maxResults, String nomQuery) {
        CriteriaBuilder cBuilder = entityManager().getCriteriaBuilder();
        CriteriaQuery<Commune> itemQuery = cBuilder.createQuery(Commune.class);
        Root<Commune> from = itemQuery.from(Commune.class);

        ArrayList<Predicate> andPredicates = new ArrayList<Predicate>(2);

        // Communes filtrées si besoin (exemple pour le VAR par défaut)
        String dep = null;
        String cle = ParamConf.ParamConfParam.COMMUNES_INSEE_LIKE_FILTRE_SQL.getCle();
        try {
            ParamConf pc = ParamConf.findParamConfsByCleEquals(cle).getSingleResult();
            dep = pc.getValeur();
        } catch (Exception e) {
            log.error("Paramètre " + cle + ", restitution de la valeur par défaut", e);
        }
        if (dep == null) {
            dep = "83%";
        }
        if (dep.trim().isEmpty()) {
            dep = "%"; // tout
        }

        Expression<String> inseePath = from.get("insee");
        Predicate depPredicate = cBuilder.like(inseePath, dep);
        andPredicates.add(depPredicate);

        Path<String> nomPath = from.get("nom");
        itemQuery.select(from).orderBy(cBuilder.asc(nomPath));

        // Commune avec le nom adéquat
        if (nomQuery != null) {
            Predicate p = cBuilder.like(cBuilder.lower(nomPath), "%" + nomQuery.toLowerCase() + "%");
            andPredicates.add(p);
        }

        Predicate andPredicate = cBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        itemQuery.where(andPredicate);

        TypedQuery<Commune> itemTypedQuery = entityManager().createQuery(itemQuery);
        return itemTypedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();

    }

}