package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantNature;
import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class TypeHydrantNatureRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantNatureRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeTypeHydrant, Integer start, Integer limit)
      throws JsonProcessingException {

    List<ReferentielModel> list =
        context
            .select(TYPE_HYDRANT_NATURE.CODE, TYPE_HYDRANT_NATURE.NOM)
            .from(TYPE_HYDRANT_NATURE)
            .join(TYPE_HYDRANT)
            .on(TYPE_HYDRANT.ID.eq(TYPE_HYDRANT_NATURE.TYPE_HYDRANT))
            .where(TYPE_HYDRANT.CODE.eq(codeTypeHydrant.toUpperCase()))
            .limit((limit == null || limit < 0) ? this.count() : limit)
            .offset((start == null || start < 0) ? 0 : start)
            .fetchInto(ReferentielModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(TYPE_HYDRANT_NATURE);
  }

  public Long getCodeNature(String code) {
    return context
        .select(TYPE_HYDRANT_NATURE.ID)
        .from(TYPE_HYDRANT_NATURE)
        .where(TYPE_HYDRANT_NATURE.CODE.eq(code))
        .fetchOneInto(Long.class);
  }

  public TypeHydrantNature getById(Long id) {
    return context
        .selectFrom(TYPE_HYDRANT_NATURE)
        .where(TYPE_HYDRANT_NATURE.ID.eq(id))
        .fetchOneInto(TypeHydrantNature.class);
  }
}
