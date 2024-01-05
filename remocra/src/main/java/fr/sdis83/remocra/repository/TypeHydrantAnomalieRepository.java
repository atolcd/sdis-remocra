package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;

import fr.sdis83.remocra.db.model.remocra.Tables;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeHydrantAnomalieRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  public Map<String, TypeHydrantAnomalie> getMapByCode() {
    return context
        .selectFrom(Tables.TYPE_HYDRANT_ANOMALIE)
        .where(Tables.TYPE_HYDRANT_ANOMALIE.CODE.isNotNull())
        .fetchMap(Tables.TYPE_HYDRANT_ANOMALIE.CODE, TypeHydrantAnomalie.class);
  }

  public List<Long> getIdsAnomaliesCTP() {
    return context
        .select(TYPE_HYDRANT_ANOMALIE.ID)
        .from(TYPE_HYDRANT_ANOMALIE)
        .join(TYPE_HYDRANT_ANOMALIE_NATURE)
        .on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
        .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES)
        .on(
            TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(
                TYPE_HYDRANT_ANOMALIE_NATURE.ID))
        .join(TYPE_HYDRANT_SAISIE)
        .on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
        .where(TYPE_HYDRANT_SAISIE.CODE.equalIgnoreCase("CTRL"))
        .fetchInto(Long.class);
  }
}
