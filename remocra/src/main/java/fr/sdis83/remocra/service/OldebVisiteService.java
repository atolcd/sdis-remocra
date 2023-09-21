package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.OldebVisite;
import fr.sdis83.remocra.domain.remocra.OldebVisiteDocument;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OldebVisiteService extends AbstractService<OldebVisite> {

  private final Logger logger = Logger.getLogger(getClass());

  public OldebVisiteService() {
    super(OldebVisite.class);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<OldebVisite> from,
      ItemFilter itemFilter) {
    // Filtrage par section
    if ("oldeb".equals(itemFilter.getFieldName())) {
      return oldebFieldPredicate(from, itemFilter);
    }
    return super.processFilterItem(itemQuery, parameters, from, itemFilter);
  }

  private Predicate oldebFieldPredicate(Root<OldebVisite> from, ItemFilter itemFilter) {
    // Création du prédicat de recherche
    CriteriaBuilder cBuilder = OldebVisite.entityManager().getCriteriaBuilder();
    Path<String> oldeb = from.get("oldeb");
    return cBuilder.equal(oldeb, itemFilter.getValue());
  }

  @Override
  protected void beforeDelete(OldebVisite attached) {
    for (OldebVisiteDocument ovD : attached.getOldebVisiteDocuments()) {
      try {
        deleteDocument(ovD.getId());
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    super.beforeDelete(attached);
  }

  public void deleteDocument(Long id) throws Exception {
    OldebVisiteDocument attached = entityManager.find(OldebVisiteDocument.class, id);
    Document d = attached.getDocument();

    // DB
    entityManager.remove(attached);
    entityManager.remove(d);
    entityManager.flush();

    // HD
    DocumentUtil.getInstance().deleteHDFile(d);
  }
}
