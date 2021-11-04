package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.deci.referentiel.TypeHydrantAnomalieModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;

public class TypeHydrantNatureAnomalieRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantNatureAnomalieRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeNature, String contexteVisite, String typeHydrant, Integer start, Integer limit) throws JsonProcessingException {

    Condition condition = this.getConditions(contexteVisite);

    List<TypeHydrantAnomalieModel> list = context
      .selectDistinct(TYPE_HYDRANT_ANOMALIE.CODE, TYPE_HYDRANT_ANOMALIE.NOM, TYPE_HYDRANT_SAISIE.CODE.as("contexte"))
      .from(TYPE_HYDRANT_ANOMALIE)
      .join(TYPE_HYDRANT_ANOMALIE_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
      .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE.NATURE))
      .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES).on(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(TYPE_HYDRANT_ANOMALIE_NATURE.ID))
      .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
      .join(TYPE_HYDRANT).on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
      .where(condition.and(TYPE_HYDRANT_NATURE.CODE.eq(codeNature.toUpperCase())).and(TYPE_HYDRANT.CODE.eq(typeHydrant.toUpperCase())))
      .orderBy(TYPE_HYDRANT_ANOMALIE.CODE)
      .limit((limit == null || limit < 0) ? this.count() : limit)
      .offset((start == null || start < 0) ? 0 : start)
      .fetchInto(TypeHydrantAnomalieModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return 999;
  }

  public Condition getConditions(String contexteVisite){

    Condition condition = DSL.trueCondition();
    if (contexteVisite != null) {
      condition = condition.and(TYPE_HYDRANT_SAISIE.CODE.eq(contexteVisite.toUpperCase()));
    }
    return condition;
  }
}
