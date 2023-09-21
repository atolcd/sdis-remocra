package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;
import static fr.sdis83.remocra.util.GlobalConstants.TypeVisite;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueModel;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;

public class HydrantVisitesRepository {

  private final DSLContext context;

  @Inject @CurrentUser Provider<UserInfo> currentUser;

  @Inject
  public HydrantVisitesRepository(DSLContext context) {
    this.context = context;
  }

  public List<HydrantVisiteModel> getAll(
      String numero,
      String contexte,
      ZonedDateTime moment,
      Boolean derniereOnly,
      Integer start,
      Integer limit)
      throws IOException {

    if (derniereOnly == null) {
      derniereOnly = false;
    }

    Condition condition = this.getConditions(contexte, moment);

    List<HydrantVisiteModel> list =
        context
            .select(
                TYPE_HYDRANT_SAISIE.NOM.as("contexte"),
                HYDRANT_VISITE.DATE,
                HYDRANT_VISITE.ID.as("identifiant"))
            .from(HYDRANT_VISITE)
            .join(TYPE_HYDRANT_SAISIE)
            .on(TYPE_HYDRANT_SAISIE.ID.eq(HYDRANT_VISITE.TYPE))
            .join(HYDRANT)
            .on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
            .where(condition.and(HYDRANT.NUMERO.eq(numero)))
            .orderBy(HYDRANT_VISITE.DATE.desc())
            .limit((derniereOnly) ? 1 : (limit == null || limit < 0) ? this.count() : limit)
            .offset((derniereOnly || start == null || start < 0) ? 0 : start)
            .fetchInto(HydrantVisiteModel.class);

    for (HydrantVisiteModel v : list) {
      v.setAnomalies(this.getListeAnomalies(v.identifiant));
    }
    return list;
  }

  public String getHydrantVisiteSpecifique(String numero, String idVisite) throws IOException {

    // Récupération des données
    HydrantVisiteSpecifiqueModel visiteData =
        context
            .select(
                HYDRANT_VISITE.DATE.as("date"),
                HYDRANT_VISITE.AGENT1.as("agent1"),
                HYDRANT_VISITE.AGENT2.as("agent2"),
                HYDRANT_VISITE.CTRL_DEBIT_PRESSION.as("ctrlDebitPression"),
                TYPE_HYDRANT_SAISIE.NOM.as("contexte"),
                HYDRANT_VISITE.DEBIT.as("debit"),
                HYDRANT_VISITE.DEBIT_MAX.as("debitMax"),
                HYDRANT_VISITE.PRESSION.as("pression"),
                HYDRANT_VISITE.PRESSION_DYN.as("pressionDyn"),
                HYDRANT_VISITE.PRESSION_DYN_DEB.as("pressionDynDeb"),
                HYDRANT_VISITE.OBSERVATIONS.as("observations"),
                HYDRANT_VISITE.ID.as("identifiant"))
            .from(HYDRANT_VISITE)
            .join(TYPE_HYDRANT_SAISIE)
            .on(TYPE_HYDRANT_SAISIE.ID.eq(HYDRANT_VISITE.TYPE))
            .join(HYDRANT)
            .on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
            .where(
                HYDRANT
                    .NUMERO
                    .eq(numero.toUpperCase())
                    .and(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite))))
            .fetchOneInto(HydrantVisiteSpecifiqueModel.class);

    // Si la visite existe bien, on met en forme les données des anomalies
    if (visiteData != null) {
      HydrantVisite visite =
          context
              .selectFrom(HYDRANT_VISITE)
              .where(HYDRANT_VISITE.ID.eq(Long.valueOf(visiteData.getIdentifiant())))
              .fetchOneInto(HydrantVisite.class);

      // Anomalies constatées => Anomalies présentes à cette visite
      List<String> anomaliesConstatees = new ArrayList<String>();
      for (String a : this.getListeAnomalies(visite.getId().toString())) {
        anomaliesConstatees.add(a);
      }
      visiteData.setAnomaliesConstatees(anomaliesConstatees);

      HydrantVisite visitePrecedente =
          context
              .selectFrom(HYDRANT_VISITE)
              .where(
                  HYDRANT_VISITE
                      .DATE
                      .lessThan(visite.getDate())
                      .and(HYDRANT_VISITE.HYDRANT.eq(visite.getHydrant())))
              .orderBy(HYDRANT_VISITE.DATE.desc())
              .limit(1)
              .fetchOneInto(HydrantVisite.class);

      // Anomalies contrôlées => Anomalies présentes à cette visite + Anomalies présentes à la
      // visite précédente non présentes à cette visite
      List<String> anomaliesControlees = new ArrayList<String>(anomaliesConstatees);
      if (visitePrecedente != null) {
        for (String anomalie : this.getListeAnomalies(visitePrecedente.getId().toString())) {
          if (visiteData.getAnomaliesConstatees().indexOf(anomalie) == -1) {
            anomaliesControlees.add(anomalie);
          }
        }
      }
      visiteData.setAnomaliesControlees(anomaliesControlees);
    }

    return new ObjectMapper().writeValueAsString(visiteData);
  }

  private Integer count() {
    return context.fetchCount(HYDRANT_VISITE);
  }

  private Condition getConditions(String contexte, ZonedDateTime moment) {

    Condition condition = DSL.trueCondition();
    if (contexte != null) {
      condition = condition.and(TYPE_HYDRANT_SAISIE.CODE.eq(contexte.toUpperCase()));
    }

    if (moment != null) {
      condition = condition.and(HYDRANT_VISITE.DATE.greaterOrEqual(moment.toInstant()));
    }
    return condition;
  }

  /**
   * @param idVisite L'identifiant de la visite
   * @return Un tableau contenant les codes des anomalies de la visite
   */
  private List<String> getListeAnomalies(String idVisite) throws IOException {
    // Comme les identifiants sont stockés au format texte et non pas sous forme de clé, on ne peut
    // pas faire directement une jointure

    // Récupération des identifiants des anomalies
    String listeId =
        context
            .select(HYDRANT_VISITE.ANOMALIES)
            .from(HYDRANT_VISITE)
            .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)))
            .fetchOneInto(String.class);

    if (listeId == null) return new ArrayList<String>();

    // Modification du format
    ObjectMapper mapper = new ObjectMapper();
    Long[] ids = mapper.readValue(listeId, Long[].class);

    // Récupération du code des anomalies
    List<String> listeCodes =
        context
            .select(TYPE_HYDRANT_ANOMALIE.CODE)
            .from(TYPE_HYDRANT_ANOMALIE)
            .where(TYPE_HYDRANT_ANOMALIE.ID.in(ids))
            .fetchInto(String.class);

    return listeCodes;
  }

  public HydrantVisite addVisite(HydrantVisite visite) {
    return addVisite(visite, this.currentUser.get().userId());
  }

  public HydrantVisite addVisite(HydrantVisite visite, Long organismeId) {
    String auteurModificationFlag =
        visite.getAuteurModificationFlag() == null ? "API" : visite.getAuteurModificationFlag();
    Long idVisite =
        context
            .insertInto(HYDRANT_VISITE)
            .set(HYDRANT_VISITE.HYDRANT, visite.getHydrant())
            .set(HYDRANT_VISITE.DATE, visite.getDate())
            .set(HYDRANT_VISITE.TYPE, visite.getType())
            .set(HYDRANT_VISITE.CTRL_DEBIT_PRESSION, visite.getCtrlDebitPression())
            .set(HYDRANT_VISITE.AGENT1, visite.getAgent1())
            .set(HYDRANT_VISITE.AGENT2, visite.getAgent2())
            .set(HYDRANT_VISITE.DEBIT, visite.getDebit())
            .set(HYDRANT_VISITE.DEBIT_MAX, visite.getDebitMax())
            .set(HYDRANT_VISITE.PRESSION, visite.getPression())
            .set(HYDRANT_VISITE.PRESSION_DYN, visite.getPressionDyn())
            .set(HYDRANT_VISITE.PRESSION_DYN_DEB, visite.getPressionDynDeb())
            .set(HYDRANT_VISITE.ANOMALIES, visite.getAnomalies())
            .set(HYDRANT_VISITE.OBSERVATIONS, visite.getObservations())
            .set(HYDRANT_VISITE.ORGANISME, organismeId)
            .set(HYDRANT_VISITE.AUTEUR_MODIFICATION_FLAG, auteurModificationFlag)
            .returning(HYDRANT_VISITE.ID)
            .fetchOne()
            .getValue(HYDRANT_VISITE.ID);

    // Si la visite ajoutée est la plus récente de son type, on fait remonter sa date dans l'hydrant
    // (si type != Non programmée)
    Long idVisitePlusRecente =
        context
            .select(HYDRANT_VISITE.ID)
            .from(HYDRANT_VISITE)
            .where(
                HYDRANT_VISITE
                    .HYDRANT
                    .eq(visite.getHydrant())
                    .and(HYDRANT_VISITE.TYPE.eq(visite.getType())))
            .orderBy(HYDRANT_VISITE.DATE.desc())
            .limit(1)
            .fetchOneInto(Long.class);

    if (idVisitePlusRecente.equals(idVisite)) {
      String codeTypeVisite =
          context
              .select(TYPE_HYDRANT_SAISIE.CODE)
              .from(TYPE_HYDRANT_SAISIE)
              .where(TYPE_HYDRANT_SAISIE.ID.eq(visite.getType()))
              .fetchOneInto(String.class);

      TableField<Record, Instant> field = null;

      TypeVisite typeVisite;
      try {
        typeVisite = TypeVisite.valueOf(codeTypeVisite);
        switch (typeVisite) {
          case CREATION:
            field = HYDRANT.DATE_CREA;
            break;

          case RECEPTION:
            field = HYDRANT.DATE_RECEP;
            break;

          case RECONNAISSANCE:
            field = HYDRANT.DATE_RECO;
            break;

          case CONTROLE:
            field = HYDRANT.DATE_CONTR;
            break;
        }
      } catch (IllegalArgumentException iae) {
        // rien à faire
      }

      if (field != null) {
        context
            .update(HYDRANT)
            .set(field, visite.getDate())
            .set(HYDRANT.ORGANISME, this.currentUser.get().userId())
            .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, auteurModificationFlag)
            .set(HYDRANT.AGENT1, visite.getAgent1())
            .set(HYDRANT.AGENT2, visite.getAgent2())
            .where(HYDRANT.ID.eq(visite.getHydrant()))
            .execute();
      }

      // Si c'est de plus un contrôle débit pression, on fait remonter ses infos dans la table
      // hydrant_pibi.
      if (visite.getCtrlDebitPression()) {
        context
            .update(HYDRANT_PIBI)
            .set(HYDRANT_PIBI.DEBIT, visite.getDebit())
            .set(HYDRANT_PIBI.DEBIT_MAX, visite.getDebitMax())
            .set(HYDRANT_PIBI.PRESSION, visite.getPression())
            .set(HYDRANT_PIBI.PRESSION_DYN, visite.getPressionDyn())
            .set(HYDRANT_PIBI.PRESSION_DYN_DEB, visite.getPressionDynDeb())
            .where(HYDRANT_PIBI.ID.eq(visite.getHydrant()))
            .execute();
      }
    }
    return context
        .select()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.ID.eq(idVisite))
        .fetchOneInto(HydrantVisite.class);
  }

  public void editVisite(HydrantVisite visite) {
    context
        .update(HYDRANT_VISITE)
        .set(HYDRANT_VISITE.CTRL_DEBIT_PRESSION, visite.getCtrlDebitPression())
        .set(HYDRANT_VISITE.AGENT1, visite.getAgent1())
        .set(HYDRANT_VISITE.AGENT2, visite.getAgent2())
        .set(HYDRANT_VISITE.DEBIT, visite.getDebit())
        .set(HYDRANT_VISITE.DEBIT_MAX, visite.getDebitMax())
        .set(HYDRANT_VISITE.PRESSION, visite.getPression())
        .set(HYDRANT_VISITE.PRESSION_DYN, visite.getPressionDyn())
        .set(HYDRANT_VISITE.PRESSION_DYN_DEB, visite.getPressionDynDeb())
        .set(HYDRANT_VISITE.ANOMALIES, visite.getAnomalies())
        .set(HYDRANT_VISITE.OBSERVATIONS, visite.getObservations())
        .set(HYDRANT_VISITE.ORGANISME, this.currentUser.get().userId())
        .set(HYDRANT_VISITE.AUTEUR_MODIFICATION_FLAG, "API")
        .where(HYDRANT_VISITE.ID.eq(visite.getId()))
        .execute();

    // Si visite CTRL, on remonte les infos débit/pression dans l'hydrant
    if (visite.getCtrlDebitPression()) {
      context
          .update(HYDRANT_PIBI)
          .set(HYDRANT_PIBI.DEBIT, visite.getDebit())
          .set(HYDRANT_PIBI.DEBIT_MAX, visite.getDebitMax())
          .set(HYDRANT_PIBI.PRESSION, visite.getPression())
          .set(HYDRANT_PIBI.PRESSION_DYN, visite.getPressionDyn())
          .set(HYDRANT_PIBI.PRESSION_DYN_DEB, visite.getPressionDynDeb())
          .where(HYDRANT_PIBI.ID.eq(visite.getHydrant()))
          .execute();
    }
  }

  public void deleteVisite(HydrantVisite visite, String codeVisite) {
    // On récupère la visite la plus récente du même type que celle à supprimer (si elle existe)
    HydrantVisite visitePlusRecenteMemeType =
        context
            .selectFrom(HYDRANT_VISITE)
            .where(
                HYDRANT_VISITE
                    .HYDRANT
                    .eq(visite.getHydrant())
                    .and(HYDRANT_VISITE.TYPE.eq(visite.getType()))
                    .and(HYDRANT_VISITE.ID.isDistinctFrom(visite.getId())))
            .orderBy(HYDRANT_VISITE.DATE.desc())
            .limit(1)
            .fetchOneInto(HydrantVisite.class);

    // Si on supprime une visite de contrôle, et qu'il en existe encore une, on reprend les valeurs
    // de cette dernière
    if (visite.getCtrlDebitPression()) {
      Integer debit = null;
      Integer debitMax = null;
      Double pression = null;
      Double pressionDyn = null;
      Double pressionDynDeb = null;

      HydrantVisite visiteDebitPressionPlusRecente =
          context
              .selectFrom(HYDRANT_VISITE)
              .where(
                  HYDRANT_VISITE
                      .HYDRANT
                      .eq(visite.getHydrant())
                      .and(HYDRANT_VISITE.TYPE.eq(visite.getType()))
                      .and(HYDRANT_VISITE.ID.isDistinctFrom(visite.getId())))
              .and(HYDRANT_VISITE.CTRL_DEBIT_PRESSION.isTrue())
              .orderBy(HYDRANT_VISITE.DATE.desc())
              .limit(1)
              .fetchOneInto(HydrantVisite.class);

      if (visiteDebitPressionPlusRecente != null
          && visiteDebitPressionPlusRecente.getCtrlDebitPression()) {
        debit = visiteDebitPressionPlusRecente.getDebit();
        debitMax = visiteDebitPressionPlusRecente.getDebitMax();
        pression = visiteDebitPressionPlusRecente.getPression();
        pressionDyn = visiteDebitPressionPlusRecente.getPressionDyn();
        pressionDynDeb = visiteDebitPressionPlusRecente.getPressionDynDeb();
      }
      context
          .update(HYDRANT_PIBI)
          .set(HYDRANT_PIBI.DEBIT, debit)
          .set(HYDRANT_PIBI.DEBIT_MAX, debitMax)
          .set(HYDRANT_PIBI.PRESSION, pression)
          .set(HYDRANT_PIBI.PRESSION_DYN, pressionDyn)
          .set(HYDRANT_PIBI.PRESSION_DYN_DEB, pressionDynDeb)
          .where(HYDRANT_PIBI.ID.eq(visite.getHydrant()))
          .execute();
    }

    // On met à jour la date dans la table hydrant
    TypeVisite typeVisite;
    TableField<Record, Instant> field = null;
    try {
      typeVisite = TypeVisite.valueOf(codeVisite);
      switch (typeVisite) {
        case CREATION:
          field = HYDRANT.DATE_CREA;
          break;

        case RECEPTION:
          field = HYDRANT.DATE_RECEP;
          break;

        case RECONNAISSANCE:
          field = HYDRANT.DATE_RECO;
          break;

        case CONTROLE:
          field = HYDRANT.DATE_CONTR;
          break;
      }
    } catch (IllegalArgumentException iae) {
      // rien à faire
    }

    if (field != null) {
      Instant date = null;
      if (visitePlusRecenteMemeType != null) {
        date = visitePlusRecenteMemeType.getDate();
      }
      context
          .update(HYDRANT)
          .set(field, date)
          .set(HYDRANT.ORGANISME, this.currentUser.get().userId())
          .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, "API")
          .where(HYDRANT.ID.eq(visite.getHydrant()))
          .execute();
    }

    // Update pour le suivi des modifications, afin que la tracabilité retrouve l'auteur des modifs
    // une fois le trigger déclenché par le DELETE
    context
        .update(HYDRANT_VISITE)
        .set(HYDRANT.ORGANISME, this.currentUser.get().userId())
        .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, "API")
        .where(HYDRANT_VISITE.ID.eq(visite.getId()))
        .execute();

    // Suppression effective de la visite
    context.deleteFrom(HYDRANT_VISITE).where(HYDRANT_VISITE.ID.eq(visite.getId())).execute();
  }

  public Integer getNbVisites(Long idHydrant, ZonedDateTime momentVisite) {
    return context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(
            HYDRANT_VISITE
                .HYDRANT
                .eq(idHydrant)
                .and(HYDRANT_VISITE.DATE.eq(momentVisite.toInstant())))
        .fetchOneInto(Integer.class);
  }

  public Integer getNbVisitesBefore(Long idHydrant, ZonedDateTime momentVisite) {
    return context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .and(HYDRANT_VISITE.DATE.lessThan(momentVisite.toInstant()))
        .fetchOneInto(Integer.class);
  }

  public String getAnomaliesLastVisite(Long idPei) {
    return context
        .select(HYDRANT_VISITE.ANOMALIES)
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idPei))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(String.class);
  }

  public HydrantVisite getFromIdNumeroPei(Long idVisite, String peiNumero) {
    return context
        .select(HYDRANT_VISITE.fields())
        .from(HYDRANT_VISITE)
        .join(HYDRANT)
        .on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
        .where(HYDRANT_VISITE.ID.eq(idVisite))
        .and(HYDRANT.NUMERO.equalIgnoreCase(peiNumero))
        .fetchOneInto(HydrantVisite.class);
  }

  public HydrantVisite getVisitePlusRecente(Long idHydrant, Instant instantVisite) {
    return context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .and(HYDRANT_VISITE.DATE.greaterThan(instantVisite))
        .fetchOneInto(HydrantVisite.class);
  }

  public HydrantVisite getLatestVisite(Long idHydrant) {
    return context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(HydrantVisite.class);
  }
}
