package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;

import javax.inject.Inject;
import fr.sdis83.remocra.web.model.authn.ParamConfModel;
import java.util.List;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;

public class ParamConfRepository {

    private final DSLContext context;

    @Inject
    public ParamConfRepository(DSLContext context) {
        this.context = context;
    }

    public List<ParamConfModel> getLdapParam() {
        return context.selectFrom(PARAM_CONF)
                .where(PARAM_CONF.CLE.likeIgnoreCase("%LDAP%"))
                .fetchInto(ParamConfModel.class);
    }
}