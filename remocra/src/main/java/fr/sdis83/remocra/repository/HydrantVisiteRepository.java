package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.GlobalConstants.TypeVisite;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_ANOMALIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.JSONUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HydrantVisiteRepository {

  @Autowired DSLContext context;

  @Autowired UtilisateurService utilisateurService;

  @Autowired private AuthoritiesUtil authUtils;

  private ObjectMapper objectMapper = new ObjectMapper();

  public HydrantVisiteRepository() {}

  @Bean
  public HydrantVisiteRepository hydrantVisiteRepository(DSLContext context) {
    return new HydrantVisiteRepository(context);
  }

  HydrantVisiteRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Ajoute une visite à un hydrant existant
   *
   * @param id identifiant du PEI. On suppose le PEI existant
   * @param visiteData données de la visite au format JSON
   */
  public HydrantVisite addVisiteFromFiche(Long id, String visiteData) throws Exception {

    if (visiteData != null && !"null".equals(visiteData)) {
      Map<String, Object> data =
          objectMapper.readValue(
              visiteData.toString(), new TypeReference<Map<String, Object>>() {});

      HydrantVisite visite = new HydrantVisite();

      // On regarde si la visite est la seule à cette date sur cet hydrant
      Integer nbVisistesMemeHeure =
          context
              .selectCount()
              .from(HYDRANT_VISITE)
              .where(
                  HYDRANT_VISITE
                      .HYDRANT
                      .eq(id)
                      .and(HYDRANT_VISITE.DATE.equal(JSONUtil.getInstant(data, "date"))))
              .fetchOneInto(Integer.class);
      if (nbVisistesMemeHeure > 0) {
        throw new Exception("Une visite est déjà présente à cette date pour cet hydrant");
      }
      // On vérifie que le type de visite est bon
      Integer nbVisites =
          context
              .selectCount()
              .from(HYDRANT_VISITE)
              .where(HYDRANT_VISITE.HYDRANT.eq(id))
              .fetchOneInto(Integer.class);

      String strTypeVisite =
          context
              .select(TYPE_HYDRANT_SAISIE.CODE)
              .from(TYPE_HYDRANT_SAISIE)
              .where(TYPE_HYDRANT_SAISIE.ID.eq(JSONUtil.getLong(data, "type")))
              .fetchOneInto(String.class);

      TypeVisite typeVisite = TypeVisite.fromCode(strTypeVisite.toUpperCase());

      if (nbVisites == 0 && !typeVisite.equals(TypeVisite.CREATION)) {
        throw new Exception(
            "Le contexte de visite doit être de type CREA (première visite du PEI)");
      } else if (nbVisites == 1 && !typeVisite.equals(TypeVisite.RECEPTION)) {
        throw new Exception(
            "Le contexte de visite doit être de type RECEP (deuxième visite du PEI)");
      } else if (nbVisites > 1
          && (!typeVisite.equals(TypeVisite.NON_PROGRAMMEE)
              && !typeVisite.equals(TypeVisite.RECONNAISSANCE)
              && !typeVisite.equals(TypeVisite.CONTROLE))) {
        throw new Exception(
            "Une visite de type "
                + typeVisite.getCode()
                + " existe déjà. Veuillez utiliser une visite de type NP, RECO ou CTRL");
      }

      visite.setHydrant(id);
      visite.setAgent1(JSONUtil.getString(data, "agent1"));
      visite.setAgent2(JSONUtil.getString(data, "agent2"));
      visite.setDate(JSONUtil.getInstant(data, "date"));
      visite.setType(JSONUtil.getLong(data, "type"));
      visite.setCtrlDebitPression(JSONUtil.getBoolean(data, "ctrl_debit_pression"));
      visite.setDebit(JSONUtil.getInteger(data, "debit"));
      visite.setDebitMax(JSONUtil.getInteger(data, "debitMax"));
      visite.setPression(JSONUtil.getDouble(data, "pression"));
      visite.setPressionDyn(JSONUtil.getDouble(data, "pressionDyn"));
      visite.setPressionDynDeb(JSONUtil.getDouble(data, "pressionDynDeb"));
      visite.setAnomalies(JSONUtil.getString(data, "anomalies"));
      visite.setObservations(JSONUtil.getString(data, "observations"));
      visite.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
      visite.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
      visite.setAuteurModificationFlag("USER");
      visite.setDebitAutre(JSONUtil.getInteger(data, "debitAutre"));
      visite.setPressionDynAutre(JSONUtil.getDouble(data, "pressionDynAutre"));
      HydrantVisite newVisite = this.addVisite(visite);
      this.launchTriggerAnomalies(id);
      return newVisite;
    }
    return null;
  }

  /**
   * Ajoute des visites aux hydrants depuis la saisie de visite d'une tournéee
   *
   * @param visiteData La liste de toutes les visites à ajouter
   * @return Un tableau JSON vide si toutes les visites ont été ajoutées, un tableau contenant la
   *     raison du rejet le cas échéant
   */
  public String addVisiteFromTournee(String json) throws IOException {
    ArrayList<String> resultats = new ArrayList<String>();
    ArrayList<Map<String, Object>> data =
        objectMapper.readValue(
            json.toString(), new TypeReference<ArrayList<Map<String, Object>>>() {});
    for (Map<String, Object> hydrantData : data) {
      Long idHydrant = JSONUtil.getLong(hydrantData, "idHydrant");
      try {
        // On réutilise la même fonction que lors de la création d'une visite depuis la fiche PEI,
        // les vérifications sont identiques
        this.addVisiteFromFiche(idHydrant, objectMapper.writeValueAsString(hydrantData));
      } catch (Exception e) {
        // Erreur survenue lors de l'ajout : on renvoie la raison de l'erreur au client
        ObjectNode erreur = objectMapper.createObjectNode();
        erreur.put("id", idHydrant);
        erreur.put("message", e.getMessage());
        resultats.add(erreur.toString());
      }
    }
    return resultats.toString();
  }

  /**
   * Renvoie la dernière visite CDP de l'hydrant qui n'est pas la visite en cours
   *
   * @param visite
   * @return HydrantVisite
   */
  private HydrantVisite getLastCDP(HydrantVisite visite) {
    return context
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
  }

  /**
   * Supprime les visites d'un pei
   *
   * @param id L'identifiant du PEI
   * @param visiteData Les identifiants des visites à supprimer
   */
  public void deleteVisiteFromFiche(Long id, String visiteData) throws IOException {
    if (visiteData != null && !"null".equals(visiteData) && !"[]".equals(visiteData)) {
      ArrayList<Long> listeVisite =
          objectMapper.readValue(visiteData.toString(), new TypeReference<ArrayList<Long>>() {});
      Collections.sort(listeVisite); // On trie par id
      Collections.reverse(listeVisite); // On commence par les visites les plus récentes

      for (Long idVisite : listeVisite) {
        HydrantVisite visite =
            context
                .select(HYDRANT_VISITE.fields())
                .from(HYDRANT_VISITE)
                .join(HYDRANT)
                .on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
                .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)).and(HYDRANT.ID.eq(id)))
                .fetchOneInto(HydrantVisite.class);
        this.deleteVisite(visite);
      }
      this.launchTriggerAnomalies(id);
    }
  }

  /**
   * Ajoute une visite à la base
   *
   * @param visite Les informations de la visite
   * @return la visite créée
   */
  public HydrantVisite addVisite(HydrantVisite visite) {
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
            .set(HYDRANT_VISITE.UTILISATEUR_MODIFICATION, visite.getUtilisateurModification())
            .set(HYDRANT_VISITE.ORGANISME, visite.getOrganisme())
            .set(HYDRANT_VISITE.AUTEUR_MODIFICATION_FLAG, visite.getAuteurModificationFlag())
            .set(HYDRANT_VISITE.DEBIT_AUTRE, visite.getDebitAutre())
            .set(HYDRANT_VISITE.PRESSION_DYN_AUTRE, visite.getPressionDynAutre())
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
      TypeVisite typeVisite = TypeVisite.fromCode(codeTypeVisite);
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
      if (field != null) {
        context
            .update(HYDRANT)
            .set(field, visite.getDate())
            .where(HYDRANT.ID.eq(visite.getHydrant()))
            .execute();
      }
      // Si c'est de plus un contrôle débit pression, on fait remonter ses infos dans la table
      // hydrant_pibi.
      if (visite.getCtrlDebitPression() != null && visite.getCtrlDebitPression()) {
        context
            .update(HYDRANT_PIBI)
            .set(HYDRANT_PIBI.DEBIT, visite.getDebit())
            .set(HYDRANT_PIBI.DEBIT_MAX, visite.getDebitMax())
            .set(HYDRANT_PIBI.PRESSION, visite.getPression())
            .set(HYDRANT_PIBI.PRESSION_DYN, visite.getPressionDyn())
            .set(HYDRANT_PIBI.PRESSION_DYN_DEB, visite.getPressionDynDeb())
            .set(HYDRANT_PIBI.DEBIT_AUTRE, visite.getDebitAutre())
            .set(HYDRANT_PIBI.PRESSION_DYN_AUTRE, visite.getPressionDynAutre())
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

  /**
   * Supprime une visite de la base
   *
   * @param visite Infos de la visite
   */
  private void deleteVisite(HydrantVisite visite) {

    String codeVisite =
        context
            .select(TYPE_HYDRANT_SAISIE.CODE)
            .from(TYPE_HYDRANT_SAISIE)
            // .join(HYDRANT_VISITE).on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
            .where(TYPE_HYDRANT_SAISIE.ID.eq(visite.getType()))
            .fetchOneInto(String.class);

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

    // Si on supprime une visite de contrôle débit/pression, et qu'il en existe encore une, on
    // reprend les valeurs de cette dernière
    if (visite.getCtrlDebitPression() != null && visite.getCtrlDebitPression()) {
      Integer debit = null;
      Integer debitMax = null;
      Double pression = null;
      Double pressionDyn = null;
      Double pressionDynDeb = null;
      Integer debitAutre = null;
      Double pressionDynAutre = null;
      HydrantVisite visiteDebitPressionPlusRecente = getLastCDP(visite);
      if (visiteDebitPressionPlusRecente != null
          && visiteDebitPressionPlusRecente.getCtrlDebitPression()) {
        debit = visiteDebitPressionPlusRecente.getDebit();
        debitMax = visiteDebitPressionPlusRecente.getDebitMax();
        pression = visiteDebitPressionPlusRecente.getPression();
        pressionDyn = visiteDebitPressionPlusRecente.getPressionDyn();
        pressionDynDeb = visiteDebitPressionPlusRecente.getPressionDynDeb();
        debitAutre = visiteDebitPressionPlusRecente.getDebitAutre();
        pressionDynAutre = visiteDebitPressionPlusRecente.getPressionDynAutre();
      }
      context
          .update(HYDRANT_PIBI)
          .set(HYDRANT_PIBI.DEBIT, debit)
          .set(HYDRANT_PIBI.DEBIT_MAX, debitMax)
          .set(HYDRANT_PIBI.PRESSION, pression)
          .set(HYDRANT_PIBI.PRESSION_DYN, pressionDyn)
          .set(HYDRANT_PIBI.PRESSION_DYN_DEB, pressionDynDeb)
          .set(HYDRANT_PIBI.DEBIT_AUTRE, debitAutre)
          .set(HYDRANT_PIBI.PRESSION_DYN_AUTRE, pressionDynAutre)
          .where(HYDRANT_PIBI.ID.eq(visite.getHydrant()))
          .execute();
    }
    // On met à jour la date dans la table hydrant
    TableField<Record, Instant> field = null;
    TypeVisite typeVisite = TypeVisite.fromCode(codeVisite);
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
    if (field != null) {
      Instant date = null;
      if (visitePlusRecenteMemeType != null) {
        date = visitePlusRecenteMemeType.getDate();
      }
      context.update(HYDRANT).set(field, date).where(HYDRANT.ID.eq(visite.getHydrant())).execute();
    }
    // Suppression effective de la visite
    context.deleteFrom(HYDRANT_VISITE).where(HYDRANT_VISITE.ID.eq(visite.getId())).execute();
  }

  /**
   * Déclenche le trigger pour calculer la disponibilité du PEI
   *
   * @param idHydrant L'identifiant du PEI
   */
  public void launchTriggerAnomalies(Long idHydrant) throws IOException {
    HydrantVisite visitePlusRecente =
        context
            .select()
            .from(HYDRANT_VISITE)
            .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
            .orderBy(HYDRANT_VISITE.DATE.desc())
            .limit(1)
            .fetchOneInto(HydrantVisite.class);

    List<Long> idAnomalieSysteme =
        context
            .select(TYPE_HYDRANT_ANOMALIE.ID)
            .from(TYPE_HYDRANT_ANOMALIE)
            .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNull())
            .fetchInto(Long.class);

    // Suppression des anomalies (hors indispo temporaires) enregistrées de cet hydrant
    context
        .deleteFrom(HYDRANT_ANOMALIES)
        .where(
            HYDRANT_ANOMALIES
                .HYDRANT
                .eq(idHydrant)
                .and(HYDRANT_ANOMALIES.ANOMALIES.notIn(idAnomalieSysteme)))
        .execute();

    // Ajout des anomalies de la visite la plus récente
    if (visitePlusRecente != null && visitePlusRecente.getAnomalies() != null) {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<ArrayList<Long>> typeRef = new TypeReference<ArrayList<Long>>() {};
      ArrayList<Long> anomalies = mapper.readValue(visitePlusRecente.getAnomalies(), typeRef);
      for (Long anomalie : anomalies) {
        context
            .insertInto(HYDRANT_ANOMALIES)
            .set(HYDRANT_ANOMALIES.HYDRANT, idHydrant)
            .set(HYDRANT_ANOMALIES.ANOMALIES, anomalie)
            .execute();
      }
    }
  }

  public Long getIdControle() {
    return context
        .select(TYPE_HYDRANT_SAISIE.ID)
        .from(TYPE_HYDRANT_SAISIE)
        .where(TYPE_HYDRANT_SAISIE.CODE.equalIgnoreCase(TypeVisite.CONTROLE.getCode()))
        .fetchOneInto(Long.class);
  }
}
