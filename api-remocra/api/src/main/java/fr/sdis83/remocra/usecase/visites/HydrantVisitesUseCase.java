package fr.sdis83.remocra.usecase.visites;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.persist.Transactional;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantNature;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantSaisie;
import fr.sdis83.remocra.repository.HydrantAnomalieRepository;
import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TypeHydrantAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.repository.TypeHydrantSaisieRepository;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.usecase.utils.DateUtils;
import fr.sdis83.remocra.usecase.utils.UseCaseUtils;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.sdis83.remocra.util.GlobalConstants.TypeVisite;

public class HydrantVisitesUseCase {

  @Inject
  HydrantVisitesRepository hydrantVisitesRepository;

  @Inject
  TypeHydrantSaisieRepository typeHydrantSaisieRepository;

  @Inject
  PeiRepository peiRepository;

  @Inject
  PeiUseCase peiUseCase;

  @Inject
  TypeHydrantAnomalieRepository typeHydrantAnomalieRepository;

  @Inject
  TypeHydrantNatureRepository typeHydrantNatureRepository;

  @Inject
  HydrantAnomalieRepository hydrantAnomalieRepository;

  @Inject
  @CurrentUser
  Provider<UserInfo> currentUser;

  /**
   * Valide les droits
   *
   * @param numero String
   */
  private void checkPeiValidity(String numero, boolean modificationEnabled) throws ResponseException {
    if (!this.peiRepository.peiExist(numero)) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if ((!this.peiUseCase.userCanEditPei(numero) && modificationEnabled) || !this.peiUseCase.isPeiAccessible(numero)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }
  }

  public List<HydrantVisiteModel> getAll(String numero, String contexte, String dateString, Boolean derniereOnly, Integer start, Integer limit) throws ResponseException, IOException {
    this.checkPeiValidity(numero, false);

    ZonedDateTime moment = null;
    if (dateString != null) {
      try {
        moment = DateUtils.getMoment(dateString);
      } catch (DateTimeParseException e) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "2000 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
      }
    }

    return this.hydrantVisitesRepository.getAll(numero, contexte, moment, derniereOnly, start, limit);
  }

  public String getHydrantVisiteSpecifique(String numero, String idVisite) throws IOException, ResponseException {
    this.checkPeiValidity(numero, false);
    return this.hydrantVisitesRepository.getHydrantVisiteSpecifique(numero, idVisite);
  }

  @Transactional
  public void addVisite(String numero, HydrantVisiteForm form) throws ResponseException {

    this.checkPeiValidity(numero, true);
    ZonedDateTime momentVisite;
    try {
      momentVisite = DateUtils.getMoment(form.date());
    } catch (DateTimeParseException dtpe) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2000 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    }

    try {
      HydrantVisite visite = new HydrantVisite();

      // On vérifie que le code de visite correspond bien à une visite
      TypeHydrantSaisie typeVisite = typeHydrantSaisieRepository.getByCode(form.contexte().toUpperCase());

      if (typeVisite == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "2001 : Le type de visite spécifié n'existe pas");
      }

      // Récupération de l'hydrant
      Hydrant hydrant = peiRepository.getByNumero(numero);
      if (hydrant == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
      }

      // On vérifie qu'il n'existe pas déjà une visite au même moment
      Integer nbVisitesMemeHeure = hydrantVisitesRepository.getNbVisites(hydrant.getId(), momentVisite);

      if (nbVisitesMemeHeure > 0) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2100 : Une visite est déjà présente à ce moment pour cet hydrant");
      }

      /*
       * On regarde si le type de visite valide en fonction du nombre de visites déjà présentes
       * 0 visites => visite de création (CREA)
       * 1 visite => visite de reconnaissance opérationnelle initiale (RECEP)
       * > 1 visites : tous les autres types (NP, RECO, CTRL)
       */
      Integer nbVisites = hydrantVisitesRepository.getNbVisitesBefore(hydrant.getId(), momentVisite);

      if (nbVisites == 0 && !form.contexte().equalsIgnoreCase(TypeVisite.CREATION.getCode())) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2101 : Le contexte de visite doit être de type CREA (première visite du PEI)");
      } else if (nbVisites == 1 && !form.contexte().equalsIgnoreCase(TypeVisite.RECEPTION.getCode())) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2102 : Le contexte de visite doit être de type RECEP (deuxième visite du PEI)");
      } else if (nbVisites > 1 &&
              Stream.of(TypeVisite.NON_PROGRAMMEE, TypeVisite.RECONNAISSANCE, TypeVisite.CONTROLE)
                      .map(TypeVisite::getCode)
                      .noneMatch(it -> it.equalsIgnoreCase(form.contexte()))) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2103 : Une visite de type " + form.contexte().toUpperCase() + " existe déjà. Veuillez utiliser une visite de type NP, RECO ou CTRL");
      }

      List<String> anomaliesControlees = new ArrayList<>();
      List<String> anomaliesConstatees = new ArrayList<>();
      for (String s : form.anomaliesControlees()) {
        anomaliesControlees.add(s.toUpperCase());
      }

      for (String s : form.anomaliesConstatees()) {
        anomaliesConstatees.add(s.toUpperCase());
      }

      if (!this.isTypeVisiteAllowed(new HydrantMaintenanceSP(hydrant.getMaintenanceDeci(), hydrant.getSpDeci()), typeVisite.getCode())) {
        throw new ResponseException(Response.Status.FORBIDDEN, "2200 : Ce type de visite n'est pas accessible pour votre organisme sur cet hydrant");
      }

      this.checkAnomalies(hydrant.getNature(), typeVisite.getCode(), anomaliesControlees, anomaliesConstatees);

      // Si débit et pression renseignés alors que ce n'est pas une visite CDP, on met les attributs à NULL
      Integer debit = null;
      Integer debitMax = null;
      Double pression = null;
      Double pressionDyn = null;
      Double pressionDynDeb = null;
      boolean ctrlDebitPression = false;
      if (form.contexte().equalsIgnoreCase(TypeVisite.CONTROLE.getCode()) && hydrant.getCode().equals("PIBI")) {
        if (form.debit() != null && form.debit() < 0)
          throw new ResponseException(Response.Status.BAD_REQUEST, "2105 : Le débit ne peut être inférieur à 0");
        if (form.debitMax() != null && form.debitMax() < 0)
          throw new ResponseException(Response.Status.BAD_REQUEST, "2106 : Le débit maximum ne peut être inférieur à 0");
        if (form.pression() != null && form.pression() < 0)
          throw new ResponseException(Response.Status.BAD_REQUEST, "2107 : La pression ne peut être inférieure à 0");
        if (form.pressionDynamique() != null && form.pressionDynamique() < 0)
          throw new ResponseException(Response.Status.BAD_REQUEST, "2108 : La pression dynamique ne peut être inférieure à 0");
        if (form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0)
          throw new ResponseException(Response.Status.BAD_REQUEST, "2109 : La pression dynamique au débit maximum ne peut être inférieure à 0");

        debit = form.debit();
        debitMax = form.debitMax();
        pression = form.pression();
        pressionDyn = form.pressionDynamique();
        pressionDynDeb = form.pressionDynamiqueDebitMax();
        if ((debit != null && debit >= 0) || (debitMax != null && debitMax >= 0) || (pression != null && pression >= 0) ||
                (pressionDyn != null && pressionDyn >= 0) || (pressionDynDeb != null && pressionDynDeb >= 0)) {
          ctrlDebitPression = true;
        }
      }

      // Liste des anomalies de cette visite
      List<Long> anomaliesIds = new ArrayList<>();

      List<TypeHydrantAnomalie> typesAnomalies = typeHydrantAnomalieRepository.getAll();

      // Conversion anomalies contrôlées et constatées Liste de code => liste d'id
      List<Long> anomaliesControleesIds =
              typesAnomalies.stream()
                      .filter(it -> anomaliesControlees.contains(it.getCode()))
                      .map(TypeHydrantAnomalie::getId)
                      .collect(Collectors.toList());
      List<Long> anomaliesConstateesIds =
              typesAnomalies.stream()
                      .filter(it -> anomaliesConstatees.contains(it.getCode()))
                      .map(TypeHydrantAnomalie::getId)
                      .collect(Collectors.toList());

      // Récupération des anomalies de la visite précédente

      String anomaliesLastVisite = hydrantVisitesRepository.getAnomaliesLastVisite(hydrant.getId());
      // On reprend les anomalies précédentes qui n'ont pas été contrôlées
      if (anomaliesLastVisite != null) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Long>> typeRef = new TypeReference<>() {
        };
        List<Long> anomaliesPrecedentes = mapper.readValue(anomaliesLastVisite, typeRef);

        for (Long l : anomaliesPrecedentes) {
          if (!anomaliesControleesIds.contains(l)) {
            anomaliesIds.add(l);
          }
        }
      }

      // On ajoute les anomalies contrôlées et constatées pour obtenir la liste des anomalies de cette visite
      anomaliesIds.addAll(anomaliesConstateesIds);

      visite.setHydrant(hydrant.getId());
      visite.setDate(momentVisite.toInstant());
      visite.setType(typeVisite.getId());
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

      this.hydrantVisitesRepository.addVisite(visite);
      this.launchTriggerAnomalies(hydrant.getId());

    } catch (IOException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue lors de l'ajout de la visite");
    }
  }

  private void launchTriggerAnomalies(Long hydrantId) throws IOException {
    HydrantVisite latestVisite = hydrantVisitesRepository.getLatestVisite(hydrantId);

    typeHydrantAnomalieRepository.deleteAnomaliesNonSysteme(hydrantId);

    // Ajout des anomalies de la visite la plus récente
    if (latestVisite != null && latestVisite.getAnomalies() != null) {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<ArrayList<Long>> typeRef = new TypeReference<>() {
      };
      List<Long> anomalies = mapper.readValue(latestVisite.getAnomalies(), typeRef);

      anomalies.forEach(anomalieId -> hydrantAnomalieRepository.insertAnomalie(hydrantId, anomalieId));
    }
  }

  @Transactional
  public void editVisite(String numero, String idVisite, HydrantVisiteSpecifiqueForm form) throws ResponseException {

    this.checkPeiValidity(numero, true);

    HydrantVisite visite = hydrantVisitesRepository.getFromIdNumeroPei(Long.valueOf(idVisite), numero);
    if (visite == null) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2003 : Aucune visite avec cet identifiant n'a été trouvée pour le numéro d'hydrant spécifié");
    }

    // On vérifie qu'il s'agit bien de la visite la plus récente
    HydrantVisite visitePlusRecente = hydrantVisitesRepository.getVisitePlusRecente(visite.getHydrant(), visite.getDate());
    if (visitePlusRecente != null) {
      throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2110 : Modification de la visite impossible : une visite plus récente est présente");
    }

    Hydrant hydrant = peiRepository.getById(visite.getHydrant());
    Long natureHydrant = hydrant.getNature();

    TypeHydrantSaisie typeVisite = typeHydrantSaisieRepository.getById(visite.getType());
    HydrantMaintenanceSP hydrantMaintenanceSP = new HydrantMaintenanceSP(hydrant.getMaintenanceDeci(), hydrant.getSpDeci());

    if (!this.isTypeVisiteAllowed(hydrantMaintenanceSP, typeVisite.getCode())) {
      throw new ResponseException(Response.Status.FORBIDDEN, "2201 : Votre organisme n'est pas autorisé à modifier une visite de type " + typeVisite.getCode() + " sur cet hydrant");
    }

    List<String> anomaliesControlees = new ArrayList<>();
    List<String> anomaliesConstatees = new ArrayList<>();
    for (String s : form.anomaliesControlees()) {
      anomaliesControlees.add(s.toUpperCase());
    }

    for (String s : form.anomaliesConstatees()) {
      anomaliesConstatees.add(s.toUpperCase());
    }

    this.checkAnomalies(natureHydrant, typeVisite.getCode(), anomaliesControlees, anomaliesConstatees);

    List<TypeHydrantAnomalie> typesAnomalies = typeHydrantAnomalieRepository.getAll();

    // Conversion anomalies contrôlées et constatées Liste de code => liste d'id
    List<Long> anomaliesConstateesIds =
            typesAnomalies.stream()
                    .filter(it -> anomaliesConstatees.contains(it.getCode()))
                    .map(TypeHydrantAnomalie::getId)
                    .collect(Collectors.toList());

    // Si débit et pression renseignés alors que ce n'est pas une visite CDP, on met les attributs à NULL
    Integer debit = null;
    Integer debitMax = null;
    Double pression = null;
    Double pressionDyn = null;
    Double pressionDynDeb = null;
    boolean ctrlDebitPression = false;
    if (TypeVisite.CONTROLE.getCode().equalsIgnoreCase(typeVisite.getCode())
            && GlobalConstants.TypeHydrant.PIBI.getCode().equalsIgnoreCase(hydrant.getCode())) {
      if (form.debit() != null && form.debit() < 0)
        throw new ResponseException(Response.Status.BAD_REQUEST, "2105 : Le débit ne peut être inférieur à 0");
      if (form.debitMax() != null && form.debitMax() < 0)
        throw new ResponseException(Response.Status.BAD_REQUEST, "2106 : Le débit maximum ne peut être inférieur à 0");
      if (form.pression() != null && form.pression() < 0)
        throw new ResponseException(Response.Status.BAD_REQUEST, "2107 : La pression ne peut être inférieure à 0");
      if (form.pressionDynamique() != null && form.pressionDynamique() < 0)
        throw new ResponseException(Response.Status.BAD_REQUEST, "2108 : La pression dynamique ne peut être inférieure à 0");
      if (form.pressionDynamiqueDebitMax() != null && form.pressionDynamiqueDebitMax() < 0)
        throw new ResponseException(Response.Status.BAD_REQUEST, "2109 : La pression dynamique au débit maximum ne peut être inférieure à 0");

      debit = form.debit();
      debitMax = form.debitMax();
      pression = form.pression();
      pressionDyn = form.pressionDynamique();
      pressionDynDeb = form.pressionDynamiqueDebitMax();
      if (debit >= 0 || debitMax >= 0 || pression >= 0 || pressionDyn >= 0 || pressionDynDeb >= 0) {
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

    HydrantVisite visite = hydrantVisitesRepository.getFromIdNumeroPei(Long.valueOf(idVisite), numero);

    if (visite == null) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2003 : Aucune visite avec cet identifiant n'a été trouvée pour le numéro d'hydrant spécifié");
    }

    TypeHydrantSaisie typeVisite = typeHydrantSaisieRepository.getById(visite.getType());

    Hydrant hydrant = peiRepository.getById(visite.getHydrant());
    HydrantMaintenanceSP hydrantMaintenanceSP = new HydrantMaintenanceSP(hydrant.getMaintenanceDeci(), hydrant.getSpDeci());

    if (!this.isTypeVisiteAllowed(hydrantMaintenanceSP, typeVisite.getCode())) {
      throw new ResponseException(Response.Status.FORBIDDEN, "2201 : Votre organisme n'est pas autorisé à modifier une visite de type " + typeVisite.getCode() + " sur cet hydrant");
    }

    // On vérifie qu'il s'agit bien de la visite la plus récente
    HydrantVisite visitePlusRecente = hydrantVisitesRepository.getVisitePlusRecente(visite.getHydrant(), visite.getDate());

    if (visitePlusRecente != null) {
      throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2110 : Modification de la visite impossible : une visite plus récente est présente");
    }

    this.hydrantVisitesRepository.deleteVisite(visite, typeVisite.getCode());
    try {
      this.launchTriggerAnomalies(visite.getHydrant());
    } catch (IOException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue lors du calcul de l'indisponibilité du PEI");
    }
  }

  /**
   * Vérifie que les anomalies contrôlées et constatées sont bien compatibles avec le type de visite spécifié, ainsi que
   * la cohérence entre les anomalies constatées et contrôlées
   *
   * @param idTypeHydrantNature Identifiant du type de saisie
   * @param codeTypeVisite      Code du type de visite
   * @param controlees          Liste de code des anomalies contrôlées
   * @param constatees          Liste de code des anomalies constatées
   * @throws ResponseException
   */
  private void checkAnomalies(Long idTypeHydrantNature, String
          codeTypeVisite, List<String> controlees, List<String> constatees) throws ResponseException {
    TypeHydrantNature typeHydrantNature = typeHydrantNatureRepository.getById(idTypeHydrantNature);

    int nbAnomaliesChecked = typeHydrantAnomalieRepository.getNbAnomaliesChecked(codeTypeVisite, typeHydrantNature.getCode(), controlees);
    if (nbAnomaliesChecked != controlees.size()) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "2002 : Une ou plusieurs anomalies contrôlées n'existent pas ou ne sont pas disponibles pour " +
              "une visite de type " + codeTypeVisite.toUpperCase());
    }

    for (String s : constatees) {
      if (!controlees.contains(s)) {
        throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, "2104 : Une ou plusieurs anomalies ont été marquées constatées sans avoir été contrôlées");
      }
    }
  }

  private static class HydrantMaintenanceSP {
    private final Long maintenanceDeci;
    private final Long servicePublicDeci;

    public HydrantMaintenanceSP(Long maintenanceDeci, Long servicePublicDeci) {
      this.maintenanceDeci = maintenanceDeci;
      this.servicePublicDeci = servicePublicDeci;
    }

    public Long getMaintenanceDeci() {
      return maintenanceDeci;
    }

    public Long getServicePublicDeci() {
      return servicePublicDeci;
    }
  }

  /**
   * Indique si l'utilisateur a les droits de modification sur la visite d'un PEI
   * La règle est la suivante:
   * - Si utilisateur est le service des eaux (seulement) de l'hydrant => Visites NP uniquement
   * - Si l'utilisateur est la maintenance DECI Ou le service public => Visites NP, CTRL et CREA uniquement
   * A ce stade, on considère que la vérification de l'accessibilité de l'hydrant a déjà été faite
   *
   * @param maintenanceSP HydrantMaintenanceSP
   * @param codeVisite    String
   * @return boolean
   */
  private boolean isTypeVisiteAllowed(HydrantMaintenanceSP maintenanceSP, String codeVisite) {
    if (Stream.of(TypeVisite.NON_PROGRAMMEE, TypeVisite.CONTROLE, TypeVisite.CREATION)
            .map(TypeVisite::getCode)
            .filter(it -> it.equalsIgnoreCase(codeVisite))
            .findAny()
            .isEmpty()
    ) {
      // Type de visite non permis par l'API
      return false;
    }

    UseCaseUtils.OrganismeIdType organisme = new UseCaseUtils.OrganismeIdType(currentUser.get().userId(), currentUser.get().type());

    // L'admin peut tout modifier
    if (UseCaseUtils.isApiAdmin(organisme)) {
      return true;
    }

    // Type CTRL ou CREA
    if (TypeVisite.CONTROLE.getCode().equalsIgnoreCase(codeVisite) || TypeVisite.CREATION.getCode().equalsIgnoreCase(codeVisite)) {

      return (
              UseCaseUtils.isServicePublicDECI(maintenanceSP.getServicePublicDeci(), organisme)
                      || UseCaseUtils.isMaintenanceDECI(maintenanceSP.getMaintenanceDeci(), organisme));
    }

    // Sinon c'est de type NP, donc toujours autorisé sur les PEI accessibles
    return true;
  }
}
