package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ORGANISME;

import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.List;

public class TypeOrganismesRepository {

  private final DSLContext context;

  @Inject
  public TypeOrganismesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(Integer start, Integer limit) throws JsonProcessingException {
    List<ReferentielModel> list = context
      .selectFrom(TYPE_ORGANISME)
      .limit((limit == null) ? this.count() : limit)
      .offset((start == null) ? 0 : start)
      .fetchInto(ReferentielModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(TYPE_ORGANISME);
  }


}
