package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;

import fr.sdis83.remocra.web.model.authn.ParamConfModel;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class ParamConfRepository {

  private final DSLContext context;

  private final String CREATION_PEI_MOBILE = "CREATION_PEI_MOBILE";

  @Inject
  public ParamConfRepository(DSLContext context) {
    this.context = context;
  }

  public List<ParamConfModel> getLdapParam() {
    return context
        .selectFrom(PARAM_CONF)
        .where(PARAM_CONF.CLE.likeIgnoreCase("%LDAP%"))
        .fetchInto(ParamConfModel.class);
  }

  public ParamConfModel getCreationPeiAppMobile() {
    return context
        .selectFrom(PARAM_CONF)
        .where(PARAM_CONF.CLE.eq(CREATION_PEI_MOBILE))
        .fetchOneInto(ParamConfModel.class);
  }

  public ParamConfModel getByCle(String cle) {
    return context
        .selectFrom(PARAM_CONF)
        .where(PARAM_CONF.CLE.eq(cle))
        .fetchOneInto(ParamConfModel.class);
  }
}
