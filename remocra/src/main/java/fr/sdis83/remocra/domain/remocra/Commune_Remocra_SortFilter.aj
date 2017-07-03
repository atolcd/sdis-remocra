package fr.sdis83.remocra.domain.remocra;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.web.message.ItemSorting;

privileged aspect Commune_Remocra_SortFilter {

    private static final Logger log = Logger.getLogger(Commune.class);

    public static List<Commune> Commune.findCommunesByNomLike(int firstResult, int maxResults, String nomQuery, ItemSorting itemSorting, Geometry paramZoneCompetenceGeom) {
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
        // Commune par zone de compétence
        if (paramZoneCompetenceGeom != null) {
            Predicate p = zoneCompetenceFieldPredicate(from, cBuilder);
            andPredicates.add(p);
        }

        // Ajout du tri par nom
        Path<?> field = from.get(itemSorting.getFieldName());
        Order order = itemSorting.isDesc() ? cBuilder.desc(field) : cBuilder.asc(field);

        // Ajout des conditions de requêtage
        Predicate andPredicate = cBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        itemQuery.where(andPredicate).orderBy(order);

        TypedQuery<Commune> itemTypedQuery = entityManager().createQuery(itemQuery);

        // Mise à jour du paramètre "zoneCompetence" avec la geometrie
        if (paramZoneCompetenceGeom != null) {
            itemTypedQuery.setParameter("zoneCompetence", paramZoneCompetenceGeom);
            itemTypedQuery.setParameter("distanceZone", Double.valueOf(0));
        }

        return itemTypedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    /**
     * Construction du prédicat sur la zone de compétence
     *
     * @param from
     * @return
     */
    private static Predicate zoneCompetenceFieldPredicate(Root<Commune> from, CriteriaBuilder cBuilder) {
        // Geometrie de la commune
        Expression<Geometry> cpPath = from.get("geometrie");
        // Zone de competence de l'utilisateur à passer en paramètre
        ParameterExpression<Geometry> zoneCompetence = cBuilder.parameter(Geometry.class, "zoneCompetence");
        ParameterExpression<Double> distanceZone = cBuilder.parameter(Double.class, "distanceZone");

        // Définition d'un buffer pouvant être agrandir la zone de compétence
        Expression<?> bufferZoneCompetence = cBuilder.function("st_buffer", Geometry.class, zoneCompetence, distanceZone);
        // Prédicat de vérification que la commune soit bien dans la zone de
        // compétence de l'utilisateur
        return cBuilder.equal(cBuilder.function("st_within", Geometry.class, cpPath, bufferZoneCompetence), Boolean.TRUE);
    }

}