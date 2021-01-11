package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_SAISIE;

public class HydrantVisitesRepository {

  private final DSLContext context;

  @Inject
  public HydrantVisitesRepository(DSLContext context) {
    this.context = context;
  }

  public String getAll(String numero, String contexte, String dateString, Boolean derniereOnly, Integer start, Integer limit) throws ResponseException, IOException {

    if(derniereOnly == null) {
      derniereOnly = false;
    }

    // Vérification format date
    Date date = null;

    try {
      if(dateString != null) {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setLenient(false);
        date = simpleDateFormat.parse(dateString);
      }

      Condition condition = this.getConditions(contexte, date);

      List<HydrantVisiteModel> list = context
        .select(TYPE_HYDRANT_SAISIE.NOM.as("contexte"), HYDRANT_VISITE.DATE, HYDRANT_VISITE.ID.as("identifiant"))
        .from(HYDRANT_VISITE)
        .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(HYDRANT_VISITE.TYPE))
        .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
        .where(condition.and(HYDRANT.NUMERO.eq(numero)))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit((derniereOnly) ? 1 : (limit == null || limit < 0) ? this.count() : limit)
        .offset((derniereOnly || start == null || start < 0) ? 0 : start)
        .fetchInto(HydrantVisiteModel.class);

      for(HydrantVisiteModel v : list) {
        v.setAnomalies(this.getListeAnomalies(v.identifiant));
      }
      return new ObjectMapper().writeValueAsString(list);
    } catch (ParseException e) {
      throw new ResponseException(HttpStatus.METHOD_NOT_ALLOWED, "La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    }
  }

  private Integer count() {
    return context.fetchCount(HYDRANT_VISITE);
  }

  private Condition getConditions(String contexte, Date date){

    Condition condition = DSL.trueCondition();
    if (contexte != null) {
      condition = condition.and(TYPE_HYDRANT_SAISIE.CODE.eq(contexte.toUpperCase()));
    }

    if(date != null) {
      condition = condition.and(HYDRANT_VISITE.DATE.greaterOrEqual(date.toInstant()));
    }
    return condition;
  }

  /**
   * @param idVisite L'identifiant de la visite
   * @return Un tableau contenant les codes des anomalies de la visite
   */
  private List<String> getListeAnomalies(String idVisite) throws IOException {
    // Comme les identifiants sont stockés au format texte et non pas sous forme de clé, on ne peut pas faire directement une jointure

    // Récupération des identifiants des anomalies
    String listeId = context
      .select(HYDRANT_VISITE.ANOMALIES)
      .from(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)))
      .fetchOneInto(String.class);

    // Modification du format
    ObjectMapper mapper = new ObjectMapper();
    Long[] ids = mapper.readValue(listeId, Long[].class);


    // Récupération du code des anomalies
    List<String> listeCodes = context
      .select(TYPE_HYDRANT_ANOMALIE.CODE)
      .from(TYPE_HYDRANT_ANOMALIE)
      .where(TYPE_HYDRANT_ANOMALIE.ID.in(ids))
      .fetchInto(String.class);

    return listeCodes;
  }
}
