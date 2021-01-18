package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_NATURE;
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

  public String getHydrantVisiteSpecifique(String numero, String idVisite) throws IOException {

    // Récupération des données
    HydrantVisiteSpecifiqueModel visiteData = context
      .select(HYDRANT_VISITE.DATE.as("date"), HYDRANT_VISITE.AGENT1.as("agent1"), HYDRANT_VISITE.AGENT2.as("agent2"),
        HYDRANT_VISITE.CTRL_DEBIT_PRESSION.as("ctrlDebitPression"), TYPE_HYDRANT_SAISIE.NOM.as("contexte"),
        HYDRANT_VISITE.DEBIT.as("debit"), HYDRANT_VISITE.DEBIT_MAX.as("debitMax"), HYDRANT_VISITE.PRESSION.as("pression"),
        HYDRANT_VISITE.PRESSION_DYN.as("pressionDyn"), HYDRANT_VISITE.PRESSION_DYN_DEB.as("pressionDynDeb"),
        HYDRANT_VISITE.OBSERVATIONS.as("observations"), HYDRANT_VISITE.ID.as("identifiant"))
      .from(HYDRANT_VISITE)
      .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(HYDRANT_VISITE.TYPE))
      .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
      .where(HYDRANT.NUMERO.eq(numero.toUpperCase()).and(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite))))
      .fetchOneInto(HydrantVisiteSpecifiqueModel.class);

    // Si la visite existe bien, on met en forme les données des anomalies
    if(visiteData != null) {
      HydrantVisite visite = context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.ID.eq(Long.valueOf(visiteData.getIdentifiant())))
        .fetchOneInto(HydrantVisite.class);

      // Anomalies constatées => Anomalies présentes à cette visite
      List<String> anomaliesConstatees = new ArrayList<String>();
      for (String a : this.getListeAnomalies(visite.getId().toString())) {
        anomaliesConstatees.add(a);
      }
      visiteData.setAnomaliesConstatees(anomaliesConstatees);

      HydrantVisite visitePrecedente = context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.DATE.lessThan(visite.getDate()).and(HYDRANT_VISITE.HYDRANT.eq(visite.getHydrant())))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(HydrantVisite.class);

      // Anomalies contrôlées => Anomalies présentes à cette visite + Anomalies présentes à la visite précédente non présentes à cette visite
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

    if(listeId == null) return new ArrayList<String>();

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

  public HydrantVisite addVisite(HydrantVisiteForm form) throws ResponseException {
    try {

      // On vérifie que la date est bien au bon format
      SimpleDateFormat simpleDateFormatClient = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      SimpleDateFormat simpleDateFormatServeur = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
      simpleDateFormatClient.setLenient(false);
      Date dateVisite = simpleDateFormatClient.parse(form.date());

      // On vérifie que le code de visite correspond bien à une visite
      Long idTypeVisite = context
        .select(TYPE_HYDRANT_SAISIE.ID)
        .from(TYPE_HYDRANT_SAISIE)
        .where(TYPE_HYDRANT_SAISIE.CODE.eq(form.contexte().toUpperCase()))
        .fetchOneInto(Long.class);

      if(idTypeVisite == null) {
        throw new ResponseException(HttpStatus.METHOD_NOT_ALLOWED, "Le type de visite spécifié n'existe pas");
      }

      // Récupération de l'hydrant
      Hydrant hydrant = context
        .select()
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.eq(form.hydrantNumero()))
        .fetchOneInto(Hydrant.class);
      if(hydrant == null) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Le numéro spécifié ne correspond à aucun hydrant");
      }

      // On vérifie qu'il n'existe pas déjà une visite à la même date
      Integer nbVisistesMemeHeure = context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(hydrant.getId()).and(HYDRANT_VISITE.DATE.eq(dateVisite.toInstant())))
        .fetchOneInto(Integer.class);

      if(nbVisistesMemeHeure > 0) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Une visite est déjà présente à cette date pour cet hydrant");
      }

      /**
       * On regarde si le type de visite valide en fonction du nombre de visites déjà présentes
       * 0 visites => visite de création (CREA)
       * 1 visite => visite de reconnaissance opérationnelle initiale (RECEP)
       * > 1 visites : tous les autres types (NP, RECO, CTRL)
       */

      Integer nbVisites = context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(hydrant.getId()))
        .fetchOneInto(Integer.class);

      if(nbVisites == 0 && !form.contexte().toUpperCase().equals("CREA")) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Le contexte de visite doit être de type CREA (première visite du PEI)");
      } else if(nbVisites == 1 && !form.contexte().toUpperCase().equals("RECEP")) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Le contexte de visite doit être de type RECEP (deuxième visite du PEI)");
      } else if(nbVisites > 1 && (!form.contexte().toUpperCase().equals("NP") && !form.contexte().toUpperCase().equals("RECO") && !form.contexte().toUpperCase().equals("CTRL"))) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Une visite de type "+form.contexte().toUpperCase()+" existe déjà. Veuillez utiliser une visite de type NP, RECO ou CTRL");
      }

      // Points contrôlés : vérifiés que les anomalies soient accessibles lors du type de visité spécifié
      String natureHydrant = context
        .select(TYPE_HYDRANT_NATURE.CODE)
        .from(TYPE_HYDRANT_NATURE)
        .where(TYPE_HYDRANT_NATURE.ID.eq(hydrant.getNature()))
        .fetchOneInto(String.class);

      ArrayList<String> anomaliesControlees = new ArrayList<String>();
      ArrayList<String> anomaliesConstatees = new ArrayList<String>();
      for(String s : form.anomaliesControlees()) {
        anomaliesControlees.add(s.toUpperCase());
      }

      for(String s : form.anomaliesConstatees()) {
        anomaliesConstatees.add(s.toUpperCase());
      }

      int nbAnomaliesChecked = context
        .selectCount()
        .from(TYPE_HYDRANT_ANOMALIE)
        .join(TYPE_HYDRANT_ANOMALIE_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
        .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES).on(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(TYPE_HYDRANT_ANOMALIE_NATURE.ID))
        .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
        .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
        .where(TYPE_HYDRANT_SAISIE.CODE.eq(form.contexte().toUpperCase())
          .and(TYPE_HYDRANT_NATURE.CODE.eq(natureHydrant))
          .and(TYPE_HYDRANT_ANOMALIE.CODE.in(anomaliesControlees)))
        .fetchOneInto(Integer.class);

      if(nbAnomaliesChecked != anomaliesControlees.size()) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Une ou plusieurs anomalies contrôlées n'existent pas où ne sont pas disponibles pour" +
          "une visite de type "+form.contexte().toUpperCase());
      }

      for(String s : anomaliesConstatees) {
        if(anomaliesControlees.indexOf(s) == -1) {
          throw new ResponseException(HttpStatus.BAD_REQUEST, "Une ou plusieurs anomalies on été marquées constatées sans avoir été contrôlées");
        }
      }

      // Si débit et pression renseignés alors que ce n'est pas une visite CDP, on met les attributs à NULL
      Integer debit = null;
      Integer debitMax = null;
      Double pression = null;
      Double pressionDyn = null;
      Double pressionDynDeb = null;
      boolean ctrlDebitPression = false;
      if(form.contexte().toUpperCase().equals("CTRL") && hydrant.getCode().equals("PIBI")) {
        if(form.debit() != null && form.debit() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "Le débit ne peut être inférieur à 0");
        if(form.debitMax() != null && form.debitMax() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "Le débit maximum ne peut être inférieur à 0");
        if(form.pression() != null && form.pression() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression ne peut être inférieure à 0");
        if(form.pressionDynamique() != null && form.pressionDynamique() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression dynamique ne peut être inférieure à 0");
        if(form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression dynamique au débit maximum ne peut être inférieure à 0");

        debit = form.debit();
        debitMax = form.debitMax();
        pression = form.pression();
        pressionDyn = form.pressionDynamique();
        pressionDynDeb = form.pressionDynamiqueDebitMax();
        if(debit >= 0 || debitMax >= 0 || pression >= 0 || pressionDyn >= 0 || pressionDynDeb >= 0) {
          ctrlDebitPression = true;
        }
      }

      // Liste des anomalies de cette visite
      ArrayList<Long> anomaliesIds = new ArrayList<Long>();

      // Conversion anomalies contrôlées et constatées Liste de code => liste d'id
      List<Long> anomaliesControleesIds = context
        .select(TYPE_HYDRANT_ANOMALIE.ID)
        .from(TYPE_HYDRANT_ANOMALIE)
        .where(TYPE_HYDRANT_ANOMALIE.CODE.in(anomaliesControlees))
        .fetchInto(Long.class);

      List<Long> anomaliesConstateesIds = context
        .select(TYPE_HYDRANT_ANOMALIE.ID)
        .from(TYPE_HYDRANT_ANOMALIE)
        .where(TYPE_HYDRANT_ANOMALIE.CODE.in(anomaliesConstatees))
        .fetchInto(Long.class);

      // Récupération des anomalies de la visite précédente
      String anomaliesP = context
        .select(HYDRANT_VISITE.ANOMALIES)
        .from(HYDRANT_VISITE)
        .join(HYDRANT).on(HYDRANT_VISITE.HYDRANT.eq(HYDRANT.ID))
        .where(HYDRANT.NUMERO.eq(form.hydrantNumero()))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(String.class);

      // On reprend les anomalies précédentes qui n'ont pas été contrôlées
      if(anomaliesP != null)
      {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<ArrayList<Long>> typeRef = new TypeReference<ArrayList<Long>>() {};
        ArrayList<Long> anomaliesPrecedentes = mapper.readValue(anomaliesP, typeRef);

        for(Long l : anomaliesPrecedentes) {
          if(anomaliesControleesIds.indexOf(l) == -1) {
            anomaliesIds.add(l);
          }
        }
      }

      // On ajoute les anomalies contrôlées et constatées pour obtenir la liste des anomalies de cette visite
      for(Long l : anomaliesConstateesIds) {
        anomaliesIds.add(l);
      }

      Long idVisite = context.insertInto(HYDRANT_VISITE)
        .set(HYDRANT_VISITE.HYDRANT, hydrant.getId())
        .set(HYDRANT_VISITE.DATE, dateVisite.toInstant())
        .set(HYDRANT_VISITE.TYPE, idTypeVisite)
        .set(HYDRANT_VISITE.CTRL_DEBIT_PRESSION, ctrlDebitPression)
        .set(HYDRANT_VISITE.AGENT1, form.agent1())
        .set(HYDRANT_VISITE.AGENT2, form.agent2())
        .set(HYDRANT_VISITE.DEBIT, debit)
        .set(HYDRANT_VISITE.DEBIT_MAX, debitMax)
        .set(HYDRANT_VISITE.PRESSION, pression)
        .set(HYDRANT_VISITE.PRESSION_DYN, pressionDyn)
        .set(HYDRANT_VISITE.PRESSION_DYN_DEB, pressionDynDeb)
        .set(HYDRANT_VISITE.ANOMALIES, anomaliesIds.toString())
        .set(HYDRANT_VISITE.OBSERVATIONS, form.observations())
        .returning(HYDRANT_VISITE.ID).fetchOne().getValue(HYDRANT_VISITE.ID);

      // Si la visite ajoutée est la plus récente de son type, on fait remonter sa date dans l'hydrant (si type != Non programmée)
      Long idVisitePlusRecente = context
        .select(HYDRANT_VISITE.ID)
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(hydrant.getId()).and(HYDRANT_VISITE.TYPE.eq(idTypeVisite)))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(Long.class);

      if(idVisitePlusRecente.equals(idVisite)) {
        TableField<Record, Instant> field = null;
        switch(form.contexte().toUpperCase()) {
          case "CREA":
            field = HYDRANT.DATE_CREA;
            break;

          case "RECEP":
            field = HYDRANT.DATE_RECEP;
            break;

          case "RECO":
            field = HYDRANT.DATE_RECO;
            break;

          case "CTRL":
            field = HYDRANT.DATE_CONTR;
            break;
        }

        if(field != null) {
          context.update(HYDRANT)
            .set(field, dateVisite.toInstant())
            .where(HYDRANT.ID.eq(hydrant.getId()))
            .execute();
        }

        // Si c'est de plus un contrôle débit pression, on fait remonter ses infos dans la table hydrant_pibi.
        if(ctrlDebitPression) {
          context.update(HYDRANT_PIBI)
            .set(HYDRANT_PIBI.DEBIT, debit)
            .set(HYDRANT_PIBI.DEBIT_MAX, debitMax)
            .set(HYDRANT_PIBI.PRESSION, pression)
            .set(HYDRANT_PIBI.PRESSION_DYN, pressionDyn)
            .set(HYDRANT_PIBI.PRESSION_DYN_DEB, pressionDynDeb)
            .where(HYDRANT_PIBI.ID.eq(hydrant.getId()))
            .execute();
        }
      }

      return context
        .select()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.ID.eq(idVisite))
        .fetchOneInto(HydrantVisite.class);

    } catch (ParseException e) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur est survenue lors de l'ajout de la visite");
  }

  public void editVisite(String numero, String idVisite, HydrantVisiteSpecifiqueForm form) throws ResponseException {
    HydrantVisite visite = context
      .select(HYDRANT_VISITE.fields())
      .from(HYDRANT_VISITE)
      .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
      .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)).and(HYDRANT.NUMERO.equalIgnoreCase(numero)))
      .fetchOneInto(HydrantVisite.class);

    if(visite == null) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Aucune visite avec cet identifiant n'a été trouvée pour le numéro d'hydant spécifié");
    }

    // On vérifie qu'il s'agit bien de la visite la plus récente
    HydrantVisite visitePlusRecente = context
      .select()
      .from(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.HYDRANT.eq(visite.getHydrant()).and(HYDRANT_VISITE.DATE.greaterThan(visite.getDate())))
      .fetchOneInto(HydrantVisite.class);

    if(visitePlusRecente != null) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Modification de la visite impossible : une visite plus récente est présente");
    }

    // Vérification des anomalies
    Long natureHydrant = context
        .select(TYPE_HYDRANT_NATURE.ID)
        .from(TYPE_HYDRANT_NATURE)
        .join(HYDRANT).on(HYDRANT.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
        .where(HYDRANT.ID.eq(visite.getHydrant()))
        .fetchOneInto(Long.class);

    String codeTypeVisite = context
      .select(TYPE_HYDRANT_SAISIE.CODE)
      .from(TYPE_HYDRANT_SAISIE)
      .where(TYPE_HYDRANT_SAISIE.ID.eq(visite.getType()))
      .fetchOneInto(String.class);

    ArrayList<String> anomaliesControlees = new ArrayList<String>();
    ArrayList<String> anomaliesConstatees = new ArrayList<String>();
    for(String s : form.anomaliesControlees()) {
      anomaliesControlees.add(s.toUpperCase());
    }

    for(String s : form.anomaliesConstatees()) {
      anomaliesConstatees.add(s.toUpperCase());
    }

    this.checkAnomalies(natureHydrant, codeTypeVisite, anomaliesControlees, anomaliesConstatees);

    // Conversion anomalies contrôlées et constatées Liste de code => liste d'id
    List<Long> anomaliesConstateesIds = context
      .select(TYPE_HYDRANT_ANOMALIE.ID)
      .from(TYPE_HYDRANT_ANOMALIE)
      .where(TYPE_HYDRANT_ANOMALIE.CODE.in(anomaliesConstatees))
      .fetchInto(Long.class);

    String codeHydrant = context
      .select(HYDRANT.CODE)
      .from(HYDRANT)
      .where(HYDRANT.ID.eq(visite.getHydrant()))
      .fetchOneInto(String.class);

    // Si débit et pression renseignés alors que ce n'est pas une visite CDP, on met les attributs à NULL
    Integer debit = null;
    Integer debitMax = null;
    Double pression = null;
    Double pressionDyn = null;
    Double pressionDynDeb = null;
    boolean ctrlDebitPression = false;
    if(codeTypeVisite.toUpperCase().equals("CTRL") && codeHydrant.equals("PIBI")) {
      if(form.debit() != null && form.debit() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "Le débit ne peut être inférieur à 0");
      if(form.debitMax() != null && form.debitMax() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "Le débit maximum ne peut être inférieur à 0");
      if(form.pression() != null && form.pression() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression ne peut être inférieure à 0");
      if(form.pressionDynamique() != null && form.pressionDynamique() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression dynamique ne peut être inférieure à 0");
      if(form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0) throw new ResponseException(HttpStatus.BAD_REQUEST, "La pression dynamique au débit maximum ne peut être inférieure à 0");

      debit = form.debit();
      debitMax = form.debitMax();
      pression = form.pression();
      pressionDyn = form.pressionDynamique();
      pressionDynDeb = form.pressionDynamiqueDebitMax();
      if(debit >= 0 || debitMax >= 0 || pression >= 0 || pressionDyn >= 0 || pressionDynDeb >= 0) {
        ctrlDebitPression = true;
      }
    }

    // Mise à jour de la visite en base
    context.update(HYDRANT_VISITE)
    .set(HYDRANT_VISITE.CTRL_DEBIT_PRESSION, ctrlDebitPression)
    .set(HYDRANT_VISITE.AGENT1, form.agent1())
    .set(HYDRANT_VISITE.AGENT2, form.agent2())
    .set(HYDRANT_VISITE.DEBIT, debit)
    .set(HYDRANT_VISITE.DEBIT_MAX, debitMax)
    .set(HYDRANT_VISITE.PRESSION, pression)
    .set(HYDRANT_VISITE.PRESSION_DYN, pressionDyn)
    .set(HYDRANT_VISITE.PRESSION_DYN_DEB, pressionDynDeb)
    .set(HYDRANT_VISITE.ANOMALIES, anomaliesConstateesIds.toString())
    .set(HYDRANT_VISITE.OBSERVATIONS, form.observations())
    .where(HYDRANT_VISITE.ID.eq(visite.getId()))
    .execute();

    // Si visite CTRL, on remonte les infos débit/pression dans l'hydrant
    if(ctrlDebitPression) {
      context.update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.DEBIT, debit)
        .set(HYDRANT_PIBI.DEBIT_MAX, debitMax)
        .set(HYDRANT_PIBI.PRESSION, pression)
        .set(HYDRANT_PIBI.PRESSION_DYN, pressionDyn)
        .set(HYDRANT_PIBI.PRESSION_DYN_DEB, pressionDynDeb)
        .where(HYDRANT_PIBI.ID.eq(visite.getHydrant()))
        .execute();
    }
  }

  /**
   * Vérifie que les anomalies contrôlées et constatées sont bien compatibles avec le type de visite spécifié, ainsi que
   * la cohérence entre les anomalies constatées et contrôlées
   * @param idTypeHydrantNature Identifiant du type de saisie
   * @param codeTypeVisite Code du type de visite
   * @param controlees Liste de code des anomalies contrôlées
   * @param constatees Liste de code des anomalies constatées
   * @throws ResponseException
   */
  private void checkAnomalies(Long idTypeHydrantNature, String codeTypeVisite, ArrayList<String> controlees, ArrayList<String> constatees) throws ResponseException {
    String natureHydrant = context
        .select(TYPE_HYDRANT_NATURE.CODE)
        .from(TYPE_HYDRANT_NATURE)
        .where(TYPE_HYDRANT_NATURE.ID.eq(idTypeHydrantNature))
        .fetchOneInto(String.class);

    int nbAnomaliesChecked = context
      .selectCount()
      .from(TYPE_HYDRANT_ANOMALIE)
      .join(TYPE_HYDRANT_ANOMALIE_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
      .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES).on(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(TYPE_HYDRANT_ANOMALIE_NATURE.ID))
      .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
      .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
      .where(TYPE_HYDRANT_SAISIE.CODE.eq(codeTypeVisite.toUpperCase())
        .and(TYPE_HYDRANT_NATURE.CODE.eq(natureHydrant))
        .and(TYPE_HYDRANT_ANOMALIE.CODE.in(controlees)))
      .fetchOneInto(Integer.class);

    if(nbAnomaliesChecked != controlees.size()) {
      throw new ResponseException(HttpStatus.BAD_REQUEST, "Une ou plusieurs anomalies contrôlées n'existent pas où ne sont pas disponibles pour" +
        "une visite de type "+codeTypeVisite.toUpperCase());
    }

    for(String s : constatees) {
      if(controlees.indexOf(s) == -1) {
        throw new ResponseException(HttpStatus.BAD_REQUEST, "Une ou plusieurs anomalies on été marquées constatées sans avoir été contrôlées");
      }
    }
  }
}
