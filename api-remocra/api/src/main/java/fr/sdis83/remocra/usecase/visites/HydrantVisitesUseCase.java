package fr.sdis83.remocra.usecase.visites;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.persist.Transactional;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_ANOMALIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;

public class HydrantVisitesUseCase {

  @Inject
  HydrantVisitesRepository hydrantVisitesRepository;

  @Inject
  PeiRepository peiRepository;

  @Inject
  PeiUseCase peiUseCase;

  @Inject @CurrentUser
  Provider<UserInfo> currentUser;

  private final DSLContext context;

  @Inject
  public HydrantVisitesUseCase(DSLContext context) {
        this.context = context;
    }

  /**
   * Valide les droits
   * @param numero
   * @throws ResponseException
   */
  private void checkPeiValidity(String numero, boolean modificationEnabled) throws ResponseException {
    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if((!this.peiUseCase.userCanEditPei(numero) && modificationEnabled) || !this.peiUseCase.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }
  }

  public List<HydrantVisiteModel> getAll(String numero, String contexte, String dateString, Boolean derniereOnly, Integer start, Integer limit) throws ResponseException, IOException {
    this.checkPeiValidity(numero, false);

    Date date = null;
    if(dateString != null) {
      String pattern = "yyyy-MM-dd HH:mm";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      simpleDateFormat.setLenient(false);
      try {
        date = simpleDateFormat.parse(dateString);
      } catch (ParseException e) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "2000 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
      }
    }

    return this.hydrantVisitesRepository.getAll(numero, contexte, date, derniereOnly, start, limit);
  }

  public String getHydrantVisiteSpecifique(String numero, String idVisite) throws IOException, ResponseException {
    this.checkPeiValidity(numero, false);
    return this.hydrantVisitesRepository.getHydrantVisiteSpecifique(numero, idVisite);
  }


  @Transactional
  public HydrantVisite addVisite(String numero, HydrantVisiteForm form) throws ResponseException {

    this.checkPeiValidity(numero, true);

    try {

      HydrantVisite visite = new HydrantVisite();

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
        throw new ResponseException(Response.Status.BAD_REQUEST, "2001 : Le type de visite spécifié n'existe pas");
      }

      // Récupération de l'hydrant
      Hydrant hydrant = context
        .select()
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
        .fetchOneInto(Hydrant.class);
      if(hydrant == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
      }

      // On vérifie qu'il n'existe pas déjà une visite à la même date
      Integer nbVisistesMemeHeure = context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(hydrant.getId()).and(HYDRANT_VISITE.DATE.eq(dateVisite.toInstant())))
        .fetchOneInto(Integer.class);

      if(nbVisistesMemeHeure > 0) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2100 : Une visite est déjà présente à cette date pour cet hydrant");
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
        .where(HYDRANT_VISITE.HYDRANT.eq(hydrant.getId()).and(HYDRANT_VISITE.DATE.lessThan(dateVisite.toInstant())))
        .fetchOneInto(Integer.class);

      if(nbVisites == 0 && !form.contexte().toUpperCase().equals("CREA")) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2101 : Le contexte de visite doit être de type CREA (première visite du PEI)");
      } else if(nbVisites == 1 && !form.contexte().toUpperCase().equals("RECEP")) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2102 : Le contexte de visite doit être de type RECEP (deuxième visite du PEI)");
      } else if(nbVisites > 1 && (!form.contexte().toUpperCase().equals("NP") && !form.contexte().toUpperCase().equals("RECO") && !form.contexte().toUpperCase().equals("CTRL"))) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2103 : Une visite de type "+form.contexte().toUpperCase()+" existe déjà. Veuillez utiliser une visite de type NP, RECO ou CTRL");
      }

      ArrayList<String> anomaliesControlees = new ArrayList<String>();
      ArrayList<String> anomaliesConstatees = new ArrayList<String>();
      for(String s : form.anomaliesControlees()) {
        anomaliesControlees.add(s.toUpperCase());
      }

      for(String s : form.anomaliesConstatees()) {
        anomaliesConstatees.add(s.toUpperCase());
      }

      String codeTypeVisite = context
      .select(TYPE_HYDRANT_SAISIE.CODE)
      .from(TYPE_HYDRANT_SAISIE)
      .where(TYPE_HYDRANT_SAISIE.ID.eq(idTypeVisite))
      .fetchOneInto(String.class);

      if(!this.isTypeVisiteAllowed(hydrant.getId(), codeTypeVisite)) {
        throw new ResponseException(Response.Status.FORBIDDEN, "2200 : Ce type de visite n'est pas accessible pour votre organisme sur cet hydrant");
      }

      Long idNatureHydrant = context
        .select(TYPE_HYDRANT_NATURE.ID)
        .from(TYPE_HYDRANT_NATURE)
        .join(HYDRANT).on(HYDRANT.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
        .where(HYDRANT.ID.eq(hydrant.getId()))
        .fetchOneInto(Long.class);

      this.checkAnomalies(idNatureHydrant, codeTypeVisite, anomaliesControlees, anomaliesConstatees);

      // Si débit et pression renseignés alors que ce n'est pas une visite CDP, on met les attributs à NULL
      Integer debit = null;
      Integer debitMax = null;
      Double pression = null;
      Double pressionDyn = null;
      Double pressionDynDeb = null;
      boolean ctrlDebitPression = false;
      if(form.contexte().toUpperCase().equals("CTRL") && hydrant.getCode().equals("PIBI")) {
        if(form.debit() != null && form.debit() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2105 : Le débit ne peut être inférieur à 0");
        if(form.debitMax() != null && form.debitMax() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2106 : Le débit maximum ne peut être inférieur à 0");
        if(form.pression() != null && form.pression() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2107 : La pression ne peut être inférieure à 0");
        if(form.pressionDynamique() != null && form.pressionDynamique() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2108 : La pression dynamique ne peut être inférieure à 0");
        if(form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2109 : La pression dynamique au débit maximum ne peut être inférieure à 0");

        debit = form.debit();
        debitMax = form.debitMax();
        pression = form.pression();
        pressionDyn = form.pressionDynamique();
        pressionDynDeb = form.pressionDynamiqueDebitMax();
        if((debit != null && debit >= 0) || (debitMax != null && debitMax >= 0) || (pression != null && pression >= 0) ||
          (pressionDyn != null && pressionDyn >= 0) || (pressionDynDeb != null && pressionDynDeb >= 0)) {
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
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
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

      visite.setHydrant(hydrant.getId());
      visite.setDate(dateVisite.toInstant());
      visite.setType(idTypeVisite);
      visite.setCtrlDebitPression(ctrlDebitPression);
      visite.setAgent1(form.agent1());
      visite.setAgent2(form.agent2());
      visite.setDebit(debit);
      visite.setDebitMax(debitMax);
      visite.setPression(pression);
      visite.setPressionDyn(pressionDyn);
      visite.setPressionDynDeb(pressionDynDeb);
      visite.setAnomalies(anomaliesIds.toString());
      visite.setObservations(form.observations());

      HydrantVisite newVisite = this.hydrantVisitesRepository.addVisite(visite);
      this.launchTriggerAnomalies(hydrant.getId());
      return newVisite;

    } catch (ParseException e) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2000 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    } catch (IOException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue lors de l'ajout de la visite");
    }
  }

  private void launchTriggerAnomalies(Long hydrantId) throws IOException {
    HydrantVisite visitePlusRecente = context
      .select()
      .from(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.HYDRANT.eq(hydrantId))
      .orderBy(HYDRANT_VISITE.DATE.desc())
      .limit(1)
      .fetchOneInto(HydrantVisite.class);

    Collection<Long> anomaliesSysteme = context
      .select(TYPE_HYDRANT_ANOMALIE.ID)
      .from(TYPE_HYDRANT_ANOMALIE)
      .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNull())
      .fetchInto(Long.class);

    // Suppression des anomalies (hors anomalies système) enregistrées de cet hydrant
    context
      .deleteFrom(HYDRANT_ANOMALIES)
      .where(HYDRANT_ANOMALIES.HYDRANT.eq(hydrantId).and(HYDRANT_ANOMALIES.ANOMALIES.notIn(anomaliesSysteme)))
      .execute();

    // Ajout des anomalies de la visite la plus récente
    if(visitePlusRecente != null && visitePlusRecente.getAnomalies() != null)  {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<ArrayList<Long>> typeRef = new TypeReference<ArrayList<Long>>() {};
      ArrayList<Long> anomalies = mapper.readValue(visitePlusRecente.getAnomalies(), typeRef);

      for(Long anomalie : anomalies) {
        context
          .insertInto(HYDRANT_ANOMALIES)
          .set(HYDRANT_ANOMALIES.HYDRANT, hydrantId)
          .set(HYDRANT_ANOMALIES.ANOMALIES, anomalie)
          .execute();
      }
    }
  }

  @Transactional
  public void editVisite(String numero, String idVisite, HydrantVisiteSpecifiqueForm form) throws ResponseException {

    this.checkPeiValidity(numero, true);

    HydrantVisite visite = context
      .select(HYDRANT_VISITE.fields())
      .from(HYDRANT_VISITE)
      .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
      .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)).and(HYDRANT.NUMERO.equalIgnoreCase(numero)))
      .fetchOneInto(HydrantVisite.class);

    if(visite == null) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2003 : Aucune visite avec cet identifiant n'a été trouvée pour le numéro d'hydant spécifié");
    }

    // On vérifie qu'il s'agit bien de la visite la plus récente
    HydrantVisite visitePlusRecente = context
      .select()
      .from(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.HYDRANT.eq(visite.getHydrant()).and(HYDRANT_VISITE.DATE.greaterThan(visite.getDate())))
      .fetchOneInto(HydrantVisite.class);

    if(visitePlusRecente != null) {
      throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2110 : Modification de la visite impossible : une visite plus récente est présente");
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

    if(!this.isTypeVisiteAllowed(visite.getHydrant(), codeTypeVisite)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "2201 : Votre organisme n'est pas autorisé à modifier une visite de type "+codeTypeVisite+" sur cet hydrant");
    }

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
      if(form.debit() != null && form.debit() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2105 : Le débit ne peut être inférieur à 0");
      if(form.debitMax() != null && form.debitMax() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2106 : Le débit maximum ne peut être inférieur à 0");
      if(form.pression() != null && form.pression() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2107 : La pression ne peut être inférieure à 0");
      if(form.pressionDynamique() != null && form.pressionDynamique() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2108 : La pression dynamique ne peut être inférieure à 0");
      if(form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0) throw new ResponseException(Response.Status.BAD_REQUEST, "2109 : La pression dynamique au débit maximum ne peut être inférieure à 0");

      debit = form.debit();
      debitMax = form.debitMax();
      pression = form.pression();
      pressionDyn = form.pressionDynamique();
      pressionDynDeb = form.pressionDynamiqueDebitMax();
      if(debit >= 0 || debitMax >= 0 || pression >= 0 || pressionDyn >= 0 || pressionDynDeb >= 0) {
        ctrlDebitPression = true;
      }
    }

    visite.setCtrlDebitPression(ctrlDebitPression);
    visite.setAgent1(form.agent1());
    visite.setAgent2(form.agent2());
    visite.setDebit(debit);
    visite.setDebitMax(debitMax);
    visite.setPression(pression);
    visite.setPressionDyn(pressionDyn);
    visite.setPressionDynDeb(pressionDynDeb);
    visite.setAnomalies(anomaliesConstateesIds.toString());
    visite.setObservations(form.observations());

    this.hydrantVisitesRepository.editVisite(visite);
    try {
      this.launchTriggerAnomalies(visite.getHydrant());
    } catch (IOException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue lors du calcul de l'indisponibilité du PEI");
    }
  }
  
  public void deleteVisite(String numero, String idVisite) throws ResponseException {
    this.checkPeiValidity(numero, true);

    HydrantVisite visite = context
      .select(HYDRANT_VISITE.fields())
      .from(HYDRANT_VISITE)
      .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_VISITE.HYDRANT))
      .where(HYDRANT_VISITE.ID.eq(Long.valueOf(idVisite)).and(HYDRANT.NUMERO.equalIgnoreCase(numero)))
      .fetchOneInto(HydrantVisite.class);

    if(visite == null) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2003 : Aucune visite avec cet identifiant n'a été trouvée pour le numéro d'hydant spécifié");
    }

    String codeVisite = context
      .select(TYPE_HYDRANT_SAISIE.CODE)
      .from(TYPE_HYDRANT_SAISIE)
      .join(HYDRANT_VISITE).on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
      .where(HYDRANT_VISITE.ID.eq(visite.getId()))
      .fetchOneInto(String.class);

    if(!this.isTypeVisiteAllowed(visite.getHydrant(), codeVisite)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "2201 : Votre organisme n'est pas autorisé à modifier une visite de type "+codeVisite+" sur cet hydrant");
    }

    // On vérifie qu'il s'agit bien de la visite la plus récente
    HydrantVisite visitePlusRecente = context
      .select()
      .from(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.HYDRANT.eq(visite.getHydrant()).and(HYDRANT_VISITE.DATE.greaterThan(visite.getDate())))
      .fetchOneInto(HydrantVisite.class);

    if(visitePlusRecente != null) {
      throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2110 : Modification de la visite impossible : une visite plus récente est présente");
    }

    this.hydrantVisitesRepository.deleteVisite(visite, codeVisite);
    try {
      this.launchTriggerAnomalies(visite.getHydrant());
    } catch (IOException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue lors du calcul de l'indisponibilité du PEI");
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
      throw new ResponseException(Response.Status.BAD_REQUEST, "2002 : Une ou plusieurs anomalies contrôlées n'existent pas où ne sont pas disponibles pour " +
        "une visite de type "+codeTypeVisite.toUpperCase());
    }

    for(String s : constatees) {
      if(controlees.indexOf(s) == -1) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2104 : Une ou plusieurs anomalies on été marquées constatées sans avoir été contrôlées");
      }
    }
  }

  /**
   * Indique si l'utilisateur a le droit de post une visite du type spécifié
   * La règle est la suivante:
   *  - Si utilisateur est le service des eaux (seulement) de l'hydrant => Visites NP uniquement
   *  - Si l'utilisateur est la maintenance DECI Ou le service public => Visites NP, CTRL et CREA uniquement
   *  A ce stade, on considère que la vérification de l'accessibilité de l'hydrant a déjà été faite
   * @param codeVisite
   * @return
   */
  private boolean isTypeVisiteAllowed(Long idHydrant, String codeVisite) {

    // Type de visite non permis par l'API
    if(!"NP".equalsIgnoreCase(codeVisite) && !"CTRL".equalsIgnoreCase(codeVisite) && !"CREA".equalsIgnoreCase(codeVisite)) {
      return false;
    }

    // Type CTRL ou CREA
    if("CTRL".equalsIgnoreCase(codeVisite) || "CREA".equalsIgnoreCase(codeVisite)) {

      Long userId = currentUser.get().userId();
      String userType = currentUser.get().type();

      SelectConditionStep<Record2<Long, Long>> record = context
      .select(HYDRANT.MAINTENANCE_DECI, HYDRANT.SP_DECI)
      .from(HYDRANT)
      .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
      .where(HYDRANT.ID.eq(idHydrant));

      Long maintenanceDeci = (record.fetchOne(HYDRANT.MAINTENANCE_DECI) != null) ? record.fetchOne(HYDRANT.MAINTENANCE_DECI).longValue() : null;
      Long sp_deci = (record.fetchOne(HYDRANT.SP_DECI) != null) ? record.fetchOne(HYDRANT.SP_DECI).longValue() : null;

      return (
        (sp_deci != null
          && sp_deci.equals(userId)
          && ("COMMUNE".equalsIgnoreCase(userType)
            || "EPCI".equalsIgnoreCase(userType)))
        || (maintenanceDeci != null
          && maintenanceDeci.equals(userId)
          && ("SERVICEEAUX".equalsIgnoreCase(userType)
            || "PRESTATAIRE_TECHNIQUE".equalsIgnoreCase(userType)
            || "COMMUNE".equalsIgnoreCase(userType)
            || "EPCI".equalsIgnoreCase(userType))));
    }

    // Sinon c'est de type NP, donc toujours autorisé sur les PEI accessibles
    return true;
  }
}
