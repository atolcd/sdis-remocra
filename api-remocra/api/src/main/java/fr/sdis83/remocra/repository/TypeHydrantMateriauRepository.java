package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.NameModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MATERIAU;

public class TypeHydrantMateriauRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantMateriauRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(Integer start, Integer limit) throws JsonProcessingException {

      List<NameModel> list = context
        .selectFrom(TYPE_HYDRANT_MATERIAU)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(NameModel.class);

      return new ObjectMapper().writeValueAsString(list);
    }

  private Integer count() {
    return context.fetchCount(TYPE_HYDRANT_MATERIAU);
  }

}