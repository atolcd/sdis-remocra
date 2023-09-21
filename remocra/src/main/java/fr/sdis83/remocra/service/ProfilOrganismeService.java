package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfilOrganismeService extends AbstractService<ProfilOrganisme> {

  private final Logger logger = Logger.getLogger(getClass());

  public ProfilOrganismeService() {
    super(ProfilOrganisme.class);
  }

  @Override
  protected boolean processItemSortings(
      ArrayList<Order> orders,
      ItemSorting itemSorting,
      CriteriaBuilder cBuilder,
      Root<ProfilOrganisme> from) {
    if ("typeOrganismeId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("typeOrganisme").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else {
      return super.processItemSortings(orders, itemSorting, cBuilder, from);
    }
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<ProfilOrganisme> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("typeOrganismeId".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("typeOrganisme").get("id");
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
