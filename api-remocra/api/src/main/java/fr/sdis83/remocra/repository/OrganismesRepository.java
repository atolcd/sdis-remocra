package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.web.model.authn.OrganismeModel;
import fr.sdis83.remocra.web.model.referentielsCommuns.OrganismesModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.Tables.TRANSFERTS_AUTOMATISES;
import static fr.sdis83.remocra.db.model.Tables.TYPE_ORGANISME;

public class OrganismesRepository {

  private final DSLContext context;

  @Inject
  public OrganismesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String codeNature, Integer start, Integer limit) throws JsonProcessingException {

      Condition condition = this.getConditions(codeNature);

      List<OrganismesModel> list = context
        .select(ORGANISME.CODE.as("code"), ORGANISME.NOM.as("nom"), TYPE_ORGANISME.CODE.as("codeNature"))
        .from(ORGANISME)
        .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
        .where(condition)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(OrganismesModel.class);

      return new ObjectMapper().writeValueAsString(list);
    }

  private Integer count() {
    return context.fetchCount(ORGANISME);
  }

  private Condition getConditions(String codeNature){

    Condition condition = DSL.trueCondition();
    if (codeNature != null) {
      condition = condition.and(TYPE_ORGANISME.CODE.eq(codeNature.toUpperCase()));
    }
    return condition;
  }

  public OrganismeModel readByEmail(String email) {
    OrganismeModel o = context
      .select(ORGANISME.ID, ORGANISME.EMAIL_CONTACT.as("email"), ORGANISME.PASSWORD, ORGANISME.SALT,
        TYPE_ORGANISME.ID.as("typeId"), TYPE_ORGANISME.CODE.as("type"))
      .from(ORGANISME)
      .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
      .where(ORGANISME.EMAIL_CONTACT.equalIgnoreCase(email.toUpperCase()))
      .fetchOneInto(OrganismeModel.class);

    if(o != null) {
      List<UserRoles> roles = new ArrayList<UserRoles>();

      SelectConditionStep<Record2<Boolean, Boolean>> permissions = context
        .select(TRANSFERTS_AUTOMATISES.RECUPERER, TRANSFERTS_AUTOMATISES.TRANSMETTRE)
        .from(TRANSFERTS_AUTOMATISES)
        .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(o.getTypeId()));

      if(permissions.fetchOne(TRANSFERTS_AUTOMATISES.RECUPERER).booleanValue()) {
        roles.add(UserRoles.RECEVOIR);
      }

      if(permissions.fetchOne(TRANSFERTS_AUTOMATISES.TRANSMETTRE).booleanValue()) {
        roles.add(UserRoles.TRANSMETTRE);
      }

      o.setRoles(roles);
    }

    return o;
  }
}
