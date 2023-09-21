package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.CriseIndicateur.CRISE_INDICATEUR;
import static fr.sdis83.remocra.db.model.remocra.tables.CriseIntervention.CRISE_INTERVENTION;
import static fr.sdis83.remocra.db.model.remocra.tables.Intervention.INTERVENTION;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseIndicateur;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Intervention;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterventionRepository {

  private final Logger logger = Logger.getLogger(getClass());
  @PersistenceContext private EntityManager entityManager;

  @Autowired DSLContext context;

  public InterventionRepository() {}

  public List<Intervention> getAll() {
    List<Intervention> l = context.select().from(INTERVENTION).fetchInto(Intervention.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(INTERVENTION));
  }

  public List<Intervention> getByCrise(Long id) {
    List<Intervention> l = null;
    l =
        context
            .select()
            .from(CRISE_INTERVENTION)
            .join(INTERVENTION)
            .on(CRISE_INTERVENTION.INTERVENTION.eq(INTERVENTION.ID))
            .where(CRISE_INTERVENTION.CRISE.eq(id))
            .fetchInto(Intervention.class);
    return l;
  }

  public String getXmlIndicateur(String code) {
    try {
      CriseIndicateur cri =
          context
              .select()
              .from(CRISE_INDICATEUR)
              .where(CRISE_INDICATEUR.CODE.eq(code))
              .fetchInto(CriseIndicateur.class)
              .get(0);
      String s = cri.getSourceSql();
      Object obj = entityManager.createNativeQuery(String.valueOf(s)).getSingleResult();
      return obj.toString();
    } catch (Exception e) {
      logger.error(
          "Une erreur est survenue lors de la tentative d'exécution de la requête " + code);
      logger.error(e.getMessage());
      return null;
    }
  }
}
