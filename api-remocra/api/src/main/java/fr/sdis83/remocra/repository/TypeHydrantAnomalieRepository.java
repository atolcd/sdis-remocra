package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_ANOMALIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;

public class TypeHydrantAnomalieRepository {
  private final DSLContext context;

  @Inject
  @CurrentUser
  Provider<UserInfo> currentUser;

  @Inject
  public TypeHydrantAnomalieRepository(DSLContext context) {
    this.context = context;
  }

  public List<TypeHydrantAnomalie> getAll() {
    return context.selectFrom(TYPE_HYDRANT_ANOMALIE).fetchInto(TypeHydrantAnomalie.class);
  }

  public int getNbAnomaliesChecked(String codeTypeVisite, String codeTypeHydrantNature, List<String> controlees) {
    return context
            .selectCount()
            .from(TYPE_HYDRANT_ANOMALIE)
            .join(TYPE_HYDRANT_ANOMALIE_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
            .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES).on(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(TYPE_HYDRANT_ANOMALIE_NATURE.ID))
            .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
            .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
            .where(TYPE_HYDRANT_SAISIE.CODE.equalIgnoreCase(codeTypeVisite)
                    .and(TYPE_HYDRANT_NATURE.CODE.eq(codeTypeHydrantNature))
                    .and(TYPE_HYDRANT_ANOMALIE.CODE.in(controlees)))
            .fetchOneInto(Integer.class);
  }

  public List<TypeHydrantAnomalie> getAnomaliesSysteme() {
    return context
            .selectFrom(TYPE_HYDRANT_ANOMALIE)
            .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNull())
            .fetchInto(TypeHydrantAnomalie.class);
  }

  public void deleteAnomaliesNonSysteme(Long peiId) {
    Collection<Long> anomaliesSysteme = getAnomaliesSysteme()
            .stream()
            .map(TypeHydrantAnomalie::getId)
            .collect(Collectors.toList());

    // Suppression des anomalies (hors anomalies système) enregistrées de cet hydrant
    context
            .deleteFrom(HYDRANT_ANOMALIES)
            .where(HYDRANT_ANOMALIES.HYDRANT.eq(peiId).and(HYDRANT_ANOMALIES.ANOMALIES.notIn(anomaliesSysteme)))
            .execute();
  }

  public Long getByCode(String code) {
    return context.select(TYPE_HYDRANT_ANOMALIE.ID)
      .from(TYPE_HYDRANT_ANOMALIE)
      .where(TYPE_HYDRANT_ANOMALIE.CODE.eq(code))
      .fetchOneInto(Long.class);
  }

  public List<TypeHydrantAnomalie> getAllWithCritere() {
    return context.selectFrom(TYPE_HYDRANT_ANOMALIE)
      .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())
      .fetchInto(TypeHydrantAnomalie.class);
  }

}
