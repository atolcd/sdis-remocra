package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_INDISPO_STATUT;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantIndispoStatut;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class TypeHydrantIndispoStatutRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantIndispoStatutRepository(DSLContext context) {
    this.context = context;
  }

  public TypeHydrantIndispoStatut getByCode(String code) {
    return context
        .selectFrom(TYPE_HYDRANT_INDISPO_STATUT)
        .where(TYPE_HYDRANT_INDISPO_STATUT.CODE.equalIgnoreCase(code))
        .fetchOneInto(TypeHydrantIndispoStatut.class);
  }
}
