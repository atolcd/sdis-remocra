package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;

import javax.inject.Inject;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;

public class TypeHydrantAnomalieRepository {
    private final DSLContext context;

    @Inject
    public TypeHydrantAnomalieRepository(DSLContext context) {
        this.context = context;
    }

    public Long getByCode(String code) {
        return context.select(TYPE_HYDRANT_ANOMALIE.ID)
                .from(TYPE_HYDRANT_ANOMALIE)
                .where(TYPE_HYDRANT_ANOMALIE.CODE.eq(code))
                .fetchOneInto(Long.class);
    }
}
