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