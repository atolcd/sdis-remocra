package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NIVEAU;

public class TypeHydrantNiveauRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantNiveauRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(Integer start, Integer limit) throws JsonProcessingException {

      List<ReferentielModel> list = context
        .selectFrom(TYPE_HYDRANT_NIVEAU)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(ReferentielModel.class);

      return new ObjectMapper().writeValueAsString(list);
    }

  private Integer count() {
    return context.fetchCount(TYPE_HYDRANT_NIVEAU);
  }
}
