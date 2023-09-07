package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantIndispoTemporaire;
import org.jooq.DSLContext;

import javax.inject.Inject;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE_HYDRANT;

public class HydrantIndispoTemporaireRepository {
  private final DSLContext context;

  @Inject
  public HydrantIndispoTemporaireRepository(DSLContext context) {
    this.context = context;
  }

  public HydrantIndispoTemporaire getById(Long id) {
    return context
            .selectFrom(HYDRANT_INDISPO_TEMPORAIRE)
            .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(id))
            .fetchOneInto(HydrantIndispoTemporaire.class);
  }

  public List<String> getPeiNumerosFromId(Long id) {
    return context
            .select(HYDRANT.NUMERO)
            .from(HYDRANT)
            .join(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT.eq(HYDRANT.ID))
            .join(HYDRANT_INDISPO_TEMPORAIRE).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE.eq(HYDRANT_INDISPO_TEMPORAIRE.ID))
            .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(id))
            .fetchInto(String.class);
  }
}
