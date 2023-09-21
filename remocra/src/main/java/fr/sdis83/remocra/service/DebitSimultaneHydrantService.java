package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.DebitSimultaneHydrant;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebitSimultaneHydrantService extends AbstractService<DebitSimultaneHydrant> {

  private final Logger logger = Logger.getLogger(getClass());

  public DebitSimultaneHydrantService() {
    super(DebitSimultaneHydrant.class);
  }

  @PersistenceContext protected EntityManager entityManager;

  @Bean
  public DebitSimultaneHydrantService DebitSimultaneHydrantService() {
    return new DebitSimultaneHydrantService();
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<DebitSimultaneHydrant> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("debit".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("debit").get("id");
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
