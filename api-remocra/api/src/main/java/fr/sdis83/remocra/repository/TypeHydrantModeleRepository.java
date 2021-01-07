package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MARQUE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MODELE;

public class TypeHydrantModeleRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantModeleRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeMarque, Integer start, Integer limit) throws JsonProcessingException {

    Condition condition = this.getConditions(codeMarque);

    List<ReferentielModel> list = context
      .select(TYPE_HYDRANT_MODELE.CODE, TYPE_HYDRANT_MODELE.NOM)
      .from(TYPE_HYDRANT_MODELE)
      .join(TYPE_HYDRANT_MARQUE).on(TYPE_HYDRANT_MODELE.MARQUE.eq(TYPE_HYDRANT_MARQUE.ID))
      .limit((limit == null || limit < 0) ? this.count() : limit)
      .offset((start == null || start < 0) ? 0 : start)
      .fetchInto(ReferentielModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(TYPE_HYDRANT_MODELE);
  }

  public Condition getConditions(String codeMarque){

    Condition condition = DSL.trueCondition();
    if (codeMarque != null) {
      condition = condition.and(TYPE_HYDRANT_MARQUE.CODE.eq(codeMarque.toUpperCase()));
    }
    return condition;
  }
}