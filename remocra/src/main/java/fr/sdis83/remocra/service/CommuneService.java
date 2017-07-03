package fr.sdis83.remocra.service;

import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class CommuneService extends AbstractService<Commune> {

    private final Logger logger = Logger.getLogger(getClass());

    public CommuneService() {
        super(Commune.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<Commune> from, ItemFilter itemFilter) {
        // Filtrage par geometrie
        if ("oldebGeom".equals(itemFilter.getFieldName())) {
            return oldebGeomPredicate(itemQuery, from);
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    @Override
    protected void processQuery(TypedQuery<?> itemTypedQuery, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters, List<Order> orders, Predicate[] predicates) {
        if (ItemFilter.getFilter(itemFilters, "oldebGeom") != null) {
            WKTReader reader = new WKTReader();
            Geometry geom = null;
            try {
                geom = reader.read(itemFilters.get(0).getValue());
                geom.setSRID(2154);
            } catch (ParseException e) {
                logger.error(e);
            }
            itemTypedQuery.setParameter("oldebGeom", geom);
        }
    }

    /**
     * Filtre suivant la zone de compétence de l'utilisateur connecté
     *
     * @param from
     * @return
     */
    private Predicate oldebGeomPredicate(CriteriaQuery<?> itemQuery, Root<Commune> from) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        // On passe par la FK commune et on retient les communes en intersection
        // avec l'oldeb
        Subquery<Geometry> sqCommuneGeoms = itemQuery.subquery(Geometry.class);
        Root<Commune> sqCommuneGeomsFrom = sqCommuneGeoms.from(Commune.class);
        ParameterExpression<Geometry> oldebGeom = cBuilder.parameter(Geometry.class, "oldebGeom");
        Predicate commOGintersectPred = cBuilder.equal(cBuilder.function("st_intersects", Boolean.class, sqCommuneGeomsFrom.get("geometrie"), oldebGeom), Boolean.TRUE);
        sqCommuneGeoms.select(sqCommuneGeomsFrom.<Geometry> get("geometrie"));
        sqCommuneGeoms.where(commOGintersectPred);
        return cBuilder.in(from.get("geometrie")).value(sqCommuneGeoms);
    }

}
