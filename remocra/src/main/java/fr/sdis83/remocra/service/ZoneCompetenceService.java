package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.domain.remocra.ZoneCompetence;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZoneCompetenceService extends AbstractService<ZoneCompetence> {

  @Autowired private ParametreDataProvider parametreProvider;

  public ZoneCompetenceService() {
    super(ZoneCompetence.class);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<ZoneCompetence> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    if ("query".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("nom");
      return cBuilder.like(cBuilder.lower(cpPath), "%" + itemFilter.getValue().toLowerCase() + "%");
    }
    return super.processFilterItem(itemQuery, parameters, from, itemFilter);
  }

  public Boolean check(String wkt, Integer srid, Long zoneCompetence) {
    return this.check(wkt, srid, zoneCompetence, 0);
  }

  public Boolean checkXY(Float x, Float y, Integer srid, int toleranceMeters, Long zoneCompetence) {
    String wkt = "POINT(" + x + " " + y + ")";
    return check(wkt, srid, zoneCompetence, toleranceMeters);
  }

  public Boolean check(String wkt, Integer srid, Long zoneCompetence, int toleranceMeters) {
    Geometry filter = GeometryUtil.toGeometry(wkt, srid);
    EntityManager em = this.entityManager;
    Query query =
        em.createQuery(
            "select dwithin(zc.geometrie, transform(:filter, :srid), :tolerance) from ZoneCompetence zc where zc.id = :id");
    query.setParameter("tolerance", toleranceMeters);
    query.setParameter("filter", filter);
    query.setParameter("srid", parametreProvider.get().getSridInt());
    query.setParameter("id", zoneCompetence);
    return (Boolean) query.getSingleResult();
  }

  public boolean check(Point geometrie, Long zoneCompetence, int toleranceChargementMetres) {
    EntityManager em = this.entityManager;
    Query query =
        em.createQuery(
            "select dwithin(zc.geometrie, :point, :tolerance) from ZoneCompetence zc where zc.id = :id");
    query.setParameter("tolerance", toleranceChargementMetres);
    query.setParameter("point", geometrie);
    query.setParameter("id", zoneCompetence);
    return (Boolean) query.getSingleResult();
  }

  public boolean check(Point geometrie, Long zoneCompetence) {
    EntityManager em = this.entityManager;
    Query query =
        em.createQuery(
            "select within(transform(:point, :srid), zc.geometrie) from ZoneCompetence zc where zc.id = :id");
    query.setParameter("point", geometrie);
    query.setParameter("srid", parametreProvider.get().getSridInt());
    query.setParameter("id", zoneCompetence);
    return (Boolean) query.getSingleResult();
  }
}
