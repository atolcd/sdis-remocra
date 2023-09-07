package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;

import javax.inject.Inject;

import static fr.sdis83.remocra.db.model.remocra.tables.HydrantAnomalies.HYDRANT_ANOMALIES;

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
}
