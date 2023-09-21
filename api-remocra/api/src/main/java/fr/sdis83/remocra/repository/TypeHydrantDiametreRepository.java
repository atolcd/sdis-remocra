package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE_NATURES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.model.referentielsCommuns.ReferentielModel;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class TypeHydrantDiametreRepository {

  private final DSLContext context;

  @Inject
  public TypeHydrantDiametreRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeNature, Integer start, Integer limit)
      throws JsonProcessingException {

    List<ReferentielModel> list =
        context
            .select(TYPE_HYDRANT_DIAMETRE.CODE, TYPE_HYDRANT_DIAMETRE.NOM)
            .from(TYPE_HYDRANT_DIAMETRE)
            .join(TYPE_HYDRANT_DIAMETRE_NATURES)
            .on(TYPE_HYDRANT_DIAMETRE_NATURES.TYPE_HYDRANT_DIAMETRE.eq(TYPE_HYDRANT_DIAMETRE.ID))
            .join(TYPE_HYDRANT_NATURE)
            .on(TYPE_HYDRANT_NATURE.ID.eq(TYPE_HYDRANT_DIAMETRE_NATURES.NATURES))
            .where(TYPE_HYDRANT_NATURE.CODE.eq(codeNature.toUpperCase()))
            .limit((limit == null || limit < 0) ? this.count() : limit)
            .offset((start == null || start < 0) ? 0 : start)
            .fetchInto(ReferentielModel.class);

    return new ObjectMapper().writeValueAsString(list);
  }

  private Integer count() {
    return context.fetchCount(TYPE_HYDRANT_DIAMETRE);
  }
}
