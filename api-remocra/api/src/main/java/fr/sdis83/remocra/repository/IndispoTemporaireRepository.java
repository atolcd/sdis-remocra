package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_INDISPO_STATUT;

import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantIndispoTemporaire;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireModel;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class IndispoTemporaireRepository {

  private final DSLContext context;

  @Inject @CurrentUser Provider<UserInfo> currentUser;

  @Inject
  public IndispoTemporaireRepository(DSLContext context) {
    this.context = context;
  }

  public List<IndispoTemporaireModel> getAll(
      String organismeApi, String hydrant, String statut, Integer limit, Integer start) {

    Condition condition = this.getConditions(organismeApi, hydrant, statut);
    List<IndispoTemporaireModel> liste =
        context
            .select(
                HYDRANT_INDISPO_TEMPORAIRE.ID.as("identifiant"),
                HYDRANT_INDISPO_TEMPORAIRE.DATE_DEBUT.as("date_debut"),
                HYDRANT_INDISPO_TEMPORAIRE.DATE_FIN.as("date_fin"),
                HYDRANT_INDISPO_TEMPORAIRE.MOTIF.as("motif"),
                HYDRANT_INDISPO_TEMPORAIRE.DATE_RAPPEL_DEBUT.as("date_rappel_debut"),
                HYDRANT_INDISPO_TEMPORAIRE.DATE_RAPPEL_FIN.as("date_rappel_fin"),
                TYPE_HYDRANT_INDISPO_STATUT.NOM.as("statut"),
                HYDRANT_INDISPO_TEMPORAIRE.TOTAL_HYDRANTS.as("total_hydrants"),
                HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_INDISPO.as("bascule_auto_indispo"),
                HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_DISPO.as("bascule_auto_dispo"),
                HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_INDISPO.as("mel_avant_indispo"),
                HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_DISPO.as("mel_avant_dispo"),
                HYDRANT_INDISPO_TEMPORAIRE.ORGANISME_API.as("organisme_API"))
            .from(HYDRANT_INDISPO_TEMPORAIRE)
            .join(TYPE_HYDRANT_INDISPO_STATUT)
            .on(TYPE_HYDRANT_INDISPO_STATUT.ID.eq(HYDRANT_INDISPO_TEMPORAIRE.STATUT))
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(HYDRANT_INDISPO_TEMPORAIRE.ORGANISME_API))
            .where(condition)
            .limit((limit == null || limit < 0) ? this.count() : limit)
            .offset((start == null || start < 0) ? 0 : start)
            .fetchInto(IndispoTemporaireModel.class);

    for (IndispoTemporaireModel m : liste) {
      m.setDate_debut(m.getDate_debut());
      m.setDate_fin(m.getDate_fin());
      m.setDate_rappel_debut(m.getDate_rappel_debut());
      m.setDate_rappel_fin(m.getDate_rappel_fin());
      List<String> hydrants =
          context
              .select(HYDRANT.NUMERO)
              .from(HYDRANT)
              .join(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT)
              .on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT.eq(HYDRANT.ID))
              .join(HYDRANT_INDISPO_TEMPORAIRE)
              .on(
                  HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE.eq(
                      HYDRANT_INDISPO_TEMPORAIRE.ID))
              .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(m.getIdentifiant()))
              .fetchInto(String.class);
      m.setHydrants(hydrants);
    }

    return liste;
  }

  private Integer count() {
    return context.fetchCount(HYDRANT_INDISPO_TEMPORAIRE);
  }

  private Condition getConditions(String organismeApi, String hydrant, String statut) {
    Condition condition = DSL.trueCondition();

    if (organismeApi != null) {
      condition = condition.and(ORGANISME.CODE.equalIgnoreCase(organismeApi));
    }

    if (hydrant != null) {
      List<Long> listeIT =
          context
              .selectDistinct(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE)
              .from(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT)
              .join(HYDRANT)
              .on(HYDRANT.ID.eq(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT))
              .where(HYDRANT.NUMERO.equalIgnoreCase(hydrant))
              .fetchInto(Long.class);
      condition = condition.and(HYDRANT_INDISPO_TEMPORAIRE.ID.in(listeIT));
    }

    if (statut != null) {
      condition = condition.and(TYPE_HYDRANT_INDISPO_STATUT.CODE.equalIgnoreCase(statut));
    }
    return condition;
  }

  public HydrantIndispoTemporaire addIndispoTemporaire(
      HydrantIndispoTemporaire indispo, List<String> hydrants) {

    Long idIndispo =
        context
            .insertInto(HYDRANT_INDISPO_TEMPORAIRE)
            .set(HYDRANT_INDISPO_TEMPORAIRE.DATE_DEBUT, indispo.getDateDebut())
            .set(HYDRANT_INDISPO_TEMPORAIRE.DATE_FIN, indispo.getDateFin())
            .set(HYDRANT_INDISPO_TEMPORAIRE.MOTIF, indispo.getMotif())
            .set(HYDRANT_INDISPO_TEMPORAIRE.STATUT, indispo.getStatut())
            .set(HYDRANT_INDISPO_TEMPORAIRE.TOTAL_HYDRANTS, hydrants.size())
            .set(HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_INDISPO, indispo.getBasculeAutoIndispo())
            .set(HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_DISPO, indispo.getBasculeAutoDispo())
            .set(HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_INDISPO, indispo.getMelAvantIndispo())
            .set(HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_DISPO, indispo.getMelAvantDispo())
            .set(HYDRANT_INDISPO_TEMPORAIRE.ORGANISME_API, currentUser.get().userId())
            .returning(HYDRANT_INDISPO_TEMPORAIRE.ID)
            .fetchOne()
            .getValue(HYDRANT_INDISPO_TEMPORAIRE.ID);

    for (String numero : hydrants) {
      Long idHydrant =
          context
              .select(HYDRANT.ID)
              .from(HYDRANT)
              .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
              .fetchOneInto(Long.class);

      context
          .insertInto(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT)
          .set(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE, idIndispo)
          .set(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT, idHydrant)
          .execute();
    }

    return context
        .selectFrom(HYDRANT_INDISPO_TEMPORAIRE)
        .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(idIndispo))
        .fetchOneInto(HydrantIndispoTemporaire.class);
  }

  public void editIndispoTemporaie(HydrantIndispoTemporaire indispo) {
    context
        .update(HYDRANT_INDISPO_TEMPORAIRE)
        .set(HYDRANT_INDISPO_TEMPORAIRE.DATE_DEBUT, indispo.getDateDebut())
        .set(HYDRANT_INDISPO_TEMPORAIRE.DATE_FIN, indispo.getDateFin())
        .set(HYDRANT_INDISPO_TEMPORAIRE.MOTIF, indispo.getMotif())
        .set(HYDRANT_INDISPO_TEMPORAIRE.STATUT, indispo.getStatut())
        .set(HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_DISPO, indispo.getBasculeAutoDispo())
        .set(HYDRANT_INDISPO_TEMPORAIRE.BASCULE_AUTO_INDISPO, indispo.getBasculeAutoIndispo())
        .set(HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_DISPO, indispo.getMelAvantDispo())
        .set(HYDRANT_INDISPO_TEMPORAIRE.MEL_AVANT_INDISPO, indispo.getMelAvantIndispo())
        .set(HYDRANT_INDISPO_TEMPORAIRE.ORGANISME_API, currentUser.get().userId())
        .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(indispo.getId()))
        .execute();
  }
}
