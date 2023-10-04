package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.tables.HydrantAnomalies.HYDRANT_ANOMALIES;

import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class HydrantAnomalieRepository {

  private final DSLContext context;

  @Inject
  public HydrantAnomalieRepository(DSLContext context) {
    this.context = context;
  }

  public void insertAnomalie(Long peiId, long anomalieId) {
    context
        .insertInto(HYDRANT_ANOMALIES)
        .set(HYDRANT_ANOMALIES.HYDRANT, peiId)
        .set(HYDRANT_ANOMALIES.ANOMALIES, anomalieId)
        .execute();
  }

  public List<Long> getAnomalieIdNonSysteme(Long idHydrant) {
    return context
        .select(HYDRANT_ANOMALIES.ANOMALIES)
        .from(HYDRANT_ANOMALIES)
        .join(TYPE_HYDRANT_ANOMALIE)
        .on(TYPE_HYDRANT_ANOMALIE.ID.eq(HYDRANT_ANOMALIES.ANOMALIES))
        .where(HYDRANT_ANOMALIES.HYDRANT.eq(idHydrant))
        .and(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())
        .fetchInto(Long.class);
  }
}
