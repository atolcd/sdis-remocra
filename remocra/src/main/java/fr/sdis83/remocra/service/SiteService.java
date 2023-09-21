package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Site;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SiteService extends AbstractService<Site> {

  private final Logger logger = Logger.getLogger(getClass());

  public SiteService() {
    super(Site.class);
  }

  @Bean
  public SiteService siteService() {
    return new SiteService();
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<Site> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("nom".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("nom");
      predicat = cBuilder.like(cBuilder.concat("", cpPath), "%" + itemFilter.getValue() + "%");
    } else if ("gestionnaire_site".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("gestionnaire_site").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("gestionnaire".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("gestionnaire_site").join("gestionnaire").get("id");
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
}
