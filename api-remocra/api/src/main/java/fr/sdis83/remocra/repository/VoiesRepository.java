package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.NameModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.VOIE;

public class VoiesRepository {

  private final DSLContext context;

  @Inject
  public VoiesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String insee, String nom, Integer start, Integer limit) throws JsonProcessingException {

    Condition condition = this.getConditions(nom);

    List<NameModel> list = context
      .select(VOIE.NOM)
      .from(VOIE)
      .join(COMMUNE).on(COMMUNE.ID.eq(VOIE.COMMUNE))
      .where(condition.and(COMMUNE.INSEE.eq(insee)))
      .limit((limit == null || limit < 0) ? this.count() : limit)
      .offset((start == null || start < 0) ? 0 : start)
      .fetchInto(NameModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(VOIE);
  }

  public Condition getConditions(String nom){

    Condition condition = DSL.trueCondition();
    if (nom != null) {
      condition = condition.and(VOIE.NOM.like("%"+nom.toUpperCase()+"%"));
    }
    return condition;
  }
}
