package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantResume;
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
public class HydrantResumeService extends AbstractService<HydrantResume> {

  private final Logger logger = Logger.getLogger(getClass());

  public HydrantResumeService() {
    super(HydrantResume.class);
  }

  @Bean
  public HydrantResumeService HydrantResumeService() {
    return new HydrantResumeService();
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<HydrantResume> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("code".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("code");
      predicat = cBuilder.like(cBuilder.concat("", cpPath), "%" + itemFilter.getValue() + "%");
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

  public String resume(Long id, Long idOrganismeUser, Boolean useDefault) {
    Hydrant h = Hydrant.findHydrant(id);

    StringBuilder code = new StringBuilder();
    code.append("RESUME_");
    code.append(h.getCode());
    if (useDefault) {
      code.append("_DEFAUT");
    }

    try {
      HydrantResume requete =
          HydrantResume.findHydrantResumesByCode(code.toString()).getSingleResult();
      Object obj =
          entityManager
              .createNativeQuery(
                  requete.getSourceSql().replaceAll("\\$\\{", ":").replaceAll("\\}", " "))
              .setParameter("HYDRANT_ID", id)
              .getSingleResult();
      return obj.toString();
    } catch (Exception e) {
      logger.error(
          "remocra.hydrant_resume: Une erreur est survenue lors de la tentative d'exécution de la requête "
              + code);
      logger.error(e.getMessage());
      return null;
    }
  }
}
