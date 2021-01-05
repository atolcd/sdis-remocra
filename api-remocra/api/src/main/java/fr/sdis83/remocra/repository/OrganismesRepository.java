package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.OrganismesModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;

import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.Tables.TYPE_ORGANISME;

public class OrganismesRepository {

  private final DSLContext context;

  @Inject
  public OrganismesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeNature, Integer start, Integer limit) throws JsonProcessingException {

      Condition condition = this.getConditions(codeNature);

      List<OrganismesModel> list = context
        .select(ORGANISME.CODE.as("code"), ORGANISME.NOM.as("nom"), TYPE_ORGANISME.CODE.as("codeNature"))
        .from(ORGANISME)
        .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
        .where(condition)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(OrganismesModel.class);

      return new ObjectMapper().writeValueAsString(list);
    }

  private Integer count() {
    return context.fetchCount(ORGANISME);
  }

  private Condition getConditions(String codeNature){

    Condition condition = DSL.trueCondition();
    if (codeNature != null) {
      condition = condition.and(TYPE_ORGANISME.CODE.eq(codeNature.toUpperCase()));
    }
    return condition;
  }
}
