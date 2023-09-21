package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeHydrantNatureService extends AbstractService<TypeHydrantNature> {

  private final Logger logger = Logger.getLogger(getClass());

  public TypeHydrantNatureService() {
    super(TypeHydrantNature.class);
  }

  /*@Bean
  public TypeHydrantNatureService TypeHydrantNatureServiceService() {
      return new TypeHydrantNatureService();
  }*/

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<TypeHydrantNature> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("nom".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("nom");
      predicat = cBuilder.like(cBuilder.concat("", cpPath), "%" + itemFilter.getValue() + "%");
    } else if ("typeHydrantCode".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("typeHydrant").get("code");
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
