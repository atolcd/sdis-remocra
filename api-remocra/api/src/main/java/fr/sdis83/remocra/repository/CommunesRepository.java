package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;


public class CommunesRepository {

  private final DSLContext context;

  @Inject
  public CommunesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String insee, String commune, Integer start, Integer limit) throws JsonProcessingException {

    Condition condition = this.getConditions(insee, commune);

    List<ReferentielModel> list = context
      .selectFrom(COMMUNE)
      .where(condition)
      .limit((limit == null || limit < 0) ? this.count() : limit)
      .offset((start == null || start < 0) ? 0 : start)
      .fetchInto(ReferentielModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(COMMUNE);
  }

  private Condition getConditions(String insee, String commune){

    Condition condition = DSL.trueCondition();
    if (insee != null) {
      condition = condition.and(COMMUNE.CODE.eq(insee));
    }
    if (commune != null) {
      condition = condition.and(COMMUNE.NOM.like("%"+commune.toUpperCase()+"%"));
    }
    return condition;
  }


}
