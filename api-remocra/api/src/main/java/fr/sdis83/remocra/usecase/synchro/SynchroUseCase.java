package fr.sdis83.remocra.usecase.synchro;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.IncomingRepository;
import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TourneeRepository;
import fr.sdis83.remocra.repository.TypeDroitRepository;
import fr.sdis83.remocra.repository.TypeHydrantAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureDeciRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.repository.TypeHydrantSaisieRepository;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.mobilemodel.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.mobilemodel.TourneeModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchroUseCase {
  private static final Logger logger = LoggerFactory.getLogger(SynchroUseCase.class);

  @Inject IncomingRepository incomingRepository;

  @Inject TypeDroitRepository typeDroitRepository;
  @Inject PeiRepository peiRepository;

  @Inject TypeHydrantSaisieRepository typeHydrantSaisieRepository;

  @Inject TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository;

  @Inject TypeHydrantNatureRepository typeHydrantNatureRepository;

  @Inject GestionnaireRepository gestionnaireRepository;

  @Inject TypeHydrantAnomalieRepository typeHydrantAnomalieRepository;

  @Inject TourneeRepository tourneeRepository;
  @Inject ParamConfRepository paramConfRepository;

  @Inject
  public SynchroUseCase(
      IncomingRepository incomingRepository,
      TypeDroitRepository typeDroitRepository,
      TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository,
      TypeHydrantNatureRepository typeHydrantNatureRepository,
      GestionnaireRepository gestionnaireRepository,
      TypeHydrantSaisieRepository typeHydrantSaisieRepository,
      PeiRepository peiRepository,
      TypeHydrantAnomalieRepository typeHydrantAnomalieRepository,
      TourneeRepository tourneeRepository,
      ParamConfRepository paramConfRepository) {
    this.incomingRepository = incomingRepository;
    this.typeDroitRepository = typeDroitRepository;
    this.typeHydrantNatureDeciRepository = typeHydrantNatureDeciRepository;
    this.typeHydrantNatureRepository = typeHydrantNatureRepository;
    this.gestionnaireRepository = gestionnaireRepository;
    this.typeHydrantSaisieRepository = typeHydrantSaisieRepository;
    this.peiRepository = peiRepository;
    this.typeHydrantAnomalieRepository = typeHydrantAnomalieRepository;
    this.tourneeRepository = tourneeRepository;
    this.paramConfRepository = paramConfRepository;
  }

  /**
   * Permet de spécifier si l'utilisateur peut créer un hydrant
   *
   * @return
   */
  public boolean getDroit(Long currentUserId, String typeDroit) {
    // On vérifie que l'utilisateur a bien le droit spécifié
    List<String> droits = typeDroitRepository.getDroitsUtilisateurMobile(currentUserId);

    if (!droits.contains(typeDroit)) {
      return false;
    }
    return true;
  }

  /** Permet en fonction de s'il s'agit d'un PIBI ou d'un PENA de l'ajouter en base */
  public Response insertHydrant(
      UUID idHydrant,
      UUID idGestionnaire,
      Long idNature,
      Long idNatureDeci,
      Double lon,
      Double lat,
      String code,
      String observations) {
    // On va chercher toutes les infos dont on a besoin

    // Géométrie de l'hydrant
    Geometry geometrieHydrant = incomingRepository.getGeometrieWithCoordonnnees(lon, lat);

    // Commune
    Long idCommune = incomingRepository.getCommuneWithGeometrie(geometrieHydrant);

    String nomVoie = incomingRepository.getVoie(geometrieHydrant);
    if (idCommune == null) {
      String errorCommune = "Impossible d'insérer l'hydrant : il n'est sur aucune commune connue.";
      logger.error(errorCommune);
      return Response.serverError().entity(errorCommune).build();
    }

    // Puis, on finit en faisant un insert
    String error = "Impossible d'insérer l'hydrant " + idHydrant + " dans incoming.";
    try {
      int result =
          incomingRepository.insertPei(
              idHydrant,
              idGestionnaire,
              idCommune,
              idNature,
              idNatureDeci,
              code,
              observations,
              geometrieHydrant,
              nomVoie);

      return gestionResult(
          result,
          "L'hydrant " + idHydrant + " a été inséré dans incoming.",
          "L'hydrant " + idHydrant + " est déjà dans le schéma incoming.",
          error);
    } catch (Exception e) {
      logger.error(error);
      return Response.serverError().entity(error).build();
    }
  }

  public Response insertGestionnaire(
      Long idRemocra, String codeGestionnaire, String nomGestionnaire, UUID idGestionnaire) {
    String erreur = checkErrorGestionnaire(idRemocra);
    if (erreur != null) {
      return Response.serverError().entity(erreur).build();
    }

    String error = "Impossible d'insérer le gestionnaire " + idGestionnaire + " dans incoming.";
    try {
      int result =
          incomingRepository.insertGestionnaire(
              idRemocra, codeGestionnaire, nomGestionnaire, idGestionnaire);

      return gestionResult(
          result,
          "Le gestionnaire " + idGestionnaire + " a été inséré dans incoming.",
          "Le gestionnaire " + idGestionnaire + " est déjà dans le schéma incoming.",
          error);

    } catch (Exception e) {
      logger.error(error);
      return Response.serverError().entity(error).build();
    }
  }

  private String checkErrorGestionnaire(Long idRemocra) {
    // On vérifie que idRemocra pointe bien vers un gestionnaire existant s'il n'est pas nul
    if (idRemocra != null && !gestionnaireRepository.checkExist(idRemocra)) {
      return "Le gestionnaire avec l'id " + idRemocra + " n'existe pas dans REMOcRA.";
    }

    return null;
  }

  public Response insertContact(ContactModel contactModel, UUID idContact, UUID idGestionnaire) {
    String erreur = checkErrorContact(contactModel, idContact, idGestionnaire);
    if (erreur != null) {
      logger.error(erreur);
      return Response.serverError().entity(erreur).build();
    }

    String error = "Impossible d'insérer le contact " + idContact + " dans incoming.";
    try {
      int result = incomingRepository.insertContact(contactModel, idContact, idGestionnaire);
      return gestionResult(
          result,
          "Le contact " + idContact + " a été inséré dans incoming.",
          "Le contact " + idContact + " est déjà dans le schéma incoming.",
          error);
    } catch (Exception e) {
      logger.error(error);
      return Response.serverError().entity(error).build();
    }
  }

  private String checkErrorContact(ContactModel contactModel, UUID idContact, UUID idGestionnaire) {
    if (contactModel.getIdRemocra() != null
        && !gestionnaireRepository.checkContactExist(contactModel.getIdRemocra())) {
      return "Le contact " + contactModel.getIdRemocra() + " n'existe pas.";
    }

    // GESTIONNAIRE
    if (!incomingRepository.checkGestionnaireExist(idGestionnaire)) {
      return "Le gestionnaire "
          + idGestionnaire
          + " associé au contact "
          + idContact
          + " n'existe pas.";
    }
    return null;
  }

  public Response insertContactRole(UUID idContact, Long idRole) {
    String erreur = checkErrorContactRole(idContact, idRole);
    if (erreur != null) {
      logger.error(erreur);
      return Response.serverError().entity(erreur).build();
    }

    String error =
        "Impossible d'insérer le contact_role " + idContact + " - " + idRole + " dans incoming.";

    try {
      int result = incomingRepository.insertContactRole(idContact, idRole);
      return gestionResult(
          result,
          "Le contactRole " + idContact + " - " + idRole + " a été inséré dans incoming.",
          "Le contactRole " + idContact + " - " + idRole + " est déjà dans le schéma incoming.",
          error);

    } catch (Exception e) {
      logger.error(error);
      return Response.serverError().entity(error).build();
    }
  }

  /**
   * Permet d'insérer une visite dans le schéma incoming
   *
   * @param hydrantVisiteModel
   * @return
   */
  public Response insertVisite(HydrantVisiteModel hydrantVisiteModel, boolean hasAnomalieChanges) {
    // Si les données ne sont pas valides, on retourne l'erreur concernée
    String erreur = checkError(hydrantVisiteModel);
    if (erreur != null) {
      logger.error(erreur);
      return Response.serverError().entity(erreur).build();
    }

    String error =
        "Impossible de créer la visite pour l'hydrant "
            + hydrantVisiteModel.idHydrant()
            + " dans incoming.";
    Response serverErrorBuild = Response.serverError().entity(error).build();
    try {
      int result = incomingRepository.insertVisite(hydrantVisiteModel, hasAnomalieChanges);
      switch (result) {
        case 1:
          String inserer =
              "La visite " + hydrantVisiteModel.idHydrantVisite() + " a été inséré dans incoming.";
          logger.info(inserer);
          return Response.ok().entity(inserer).build();
        case 0:
          String dejaEnBase =
              "La visite "
                  + hydrantVisiteModel.idHydrantVisite()
                  + " est déjà dans le schéma incoming.";
          logger.warn(dejaEnBase);
          return Response.ok().entity(dejaEnBase).build();
        default:
          logger.error(error);
          return serverErrorBuild;
      }
    } catch (Exception e) {
      logger.error(error);
      return serverErrorBuild;
    }
  }

  /**
   * Vérifie si les données que l'on va insérer sont dans le bon état
   *
   * @param hydrantVisiteModel
   * @return
   */
  private String checkError(HydrantVisiteModel hydrantVisiteModel) {
    // On vérifie que le type saisie est bon
    if (hydrantVisiteModel.idTypeVisite() == null) {
      return "Le type de visite n'hésite pas en base : " + hydrantVisiteModel.idTypeVisite();
    }

    // On regarde si l'hydrant est bien dans la base de données REMOcRA
    if (!peiRepository.peiExist(hydrantVisiteModel.idHydrant())) {
      return "L'hydrant avec l'identifiant " + hydrantVisiteModel.idHydrant() + " n'existe pas";
    }
    return null;
  }

  private String checkErrorContactRole(UUID idContact, Long idRole) {
    if (!gestionnaireRepository.checkRoleExist(idRole)) {
      return "Le role avec l'id " + idRole + " n'existe pas.";
    }

    // Contact
    if (!incomingRepository.checkContactExist(idContact)) {
      return "Le contact " + idContact + " associé au rôle " + idRole + " n'existe pas.";
    }
    return null;
  }

  private Response gestionResult(int result, String inserer, String dejaEnBase, String error) {
    switch (result) {
      case 1:
        logger.info(inserer);
        return Response.ok().entity(inserer).build();
      case 0:
        logger.warn(dejaEnBase);
        return Response.ok().entity(dejaEnBase).build();
      default:
        logger.error(error);
        return Response.serverError().entity(error).build();
    }
  }

  public Response insertHydrantVisiteAnomalie(UUID idHydrantVisite, Long idAnomalie) {

    String erreur = checkErrorAnomalies(idHydrantVisite, idAnomalie);
    if (erreur != null) {
      logger.error(erreur);
      return Response.serverError().entity(erreur).build();
    }

    String error =
        "Impossible d'ajouter le lien entre l'anomalie "
            + idAnomalie
            + " et la visite "
            + idHydrantVisite
            + " dans incoming.";
    try {
      int result = incomingRepository.insertHydrantVisiteAnomalie(idHydrantVisite, idAnomalie);

      return gestionResult(
          result,
          "Le lien entre l'anomalie "
              + idAnomalie
              + " et la visite "
              + idHydrantVisite
              + " a été inséré dans incoming.",
          "Le lien entre l'anomalie "
              + idAnomalie
              + " et la visite "
              + idHydrantVisite
              + " est déjà dans le schéma incoming.",
          error);

    } catch (Exception e) {
      return Response.serverError().entity(error).build();
    }
  }

  private String checkErrorAnomalies(UUID idHydrantVisite, Long idTypeAnomalie) {

    if (!incomingRepository.checkHydrantVisiteExist(idHydrantVisite)) {
      return "Impossible de trouver la visite " + idHydrantVisite + " dans incoming.";
    }

    if (idTypeAnomalie == null) {
      return "Impossible de trouver l'anomalie : l'anomalie " + idTypeAnomalie + " n'exite pas.";
    }

    return null;
  }

  public Response insertTournee(TourneeModel tourneeModel, Long idUtilisateur) {
    String erreur = checkErrorTournee(tourneeModel, idUtilisateur);
    if (erreur != null) {
      logger.error(erreur);
      return Response.serverError().entity(erreur).build();
    }

    String error =
        "Impossible d'ajouter la tournée " + tourneeModel.idRemocra() + " dans incoming.";
    try {
      int result = incomingRepository.insertTournee(tourneeModel);

      return gestionResult(
          result,
          "La tournée " + tourneeModel.idRemocra() + " a été inséré dans incoming.",
          "La tournée " + tourneeModel.idRemocra() + " est déjà dans le schéma incoming.",
          error);

    } catch (Exception e) {
      logger.error(error);
      return Response.serverError().entity(error).build();
    }
  }

  private String checkErrorTournee(TourneeModel tourneeModel, Long idUtilisateur) {
    if (!tourneeRepository.checkExist(tourneeModel.idRemocra())) {
      return "Impossible de trouver la tournée " + tourneeModel.idRemocra() + " dans REMOcRA.";
    }

    if (!tourneeRepository.getReservation(tourneeModel.idRemocra()).equals(idUtilisateur)) {
      return "L'utilisateur qui envoie la tournée "
          + tourneeModel.idRemocra()
          + " n'est pas celui qui l'a réservée.";
    }

    return null;
  }

  /**
   * S'assure que le répertoire existe en le créant si besoin.
   *
   * @param repertoire
   * @return
   * @throws SecurityException si le répertoire n'a pas pu être créé
   */
  public File ensureDirectory(String repertoire) throws SecurityException {
    File dir = new File(repertoire);
    if (!dir.exists()) {
      // Créer le répertoire
      if (!dir.mkdirs()) {
        throw new SecurityException("Impossible de créer le répertoire " + repertoire);
      }
    }
    return dir;
  }

  private void saveFileToHD(byte[] photoBytes, String repertoire, String fichier) {
    // Création du répertoire d'accueil si nécessaire
    File depotDir = ensureDirectory(repertoire);

    String targetFilePath = depotDir + File.separator + fichier;
    if (depotDir.canWrite()) {
      try (OutputStream outStream = new FileOutputStream(targetFilePath)) {
        outStream.write(photoBytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new SecurityException("Impossible de créer le fichier " + targetFilePath);
    }
  }

  public Response insertHydrantPhoto(
      Long idHydrant, Instant moment, byte[] photoBytes, String nomPhoto) throws Exception {
    // On enregistre sur le disque
    String code = idHydrant.toString() + moment.toString();
    String path =
        Path.of(paramConfRepository.getByCle(GlobalConstants.DOSSIER_DOC_HYDRANT).getValeur(), code)
            .toString();

    String dejaEnBase = "L'hydrantPhoto est déjà dans le schéma incoming.";
    // Si la photo n'existe pas, alors on l'insert
    if (incomingRepository.checkExist(idHydrant, path)) {
      logger.warn(dejaEnBase);
      return Response.ok().entity(dejaEnBase).build();
    }

    saveFileToHD(photoBytes, path, nomPhoto);

    // Puis on sauvegarde dans incoming
    int res = incomingRepository.insertHydrantPhoto(idHydrant, path, moment, nomPhoto);
    return gestionResult(
        res,
        "L'hydrantPhoto a bien été inséré un hydrantPhoto dans incoming.",
        dejaEnBase,
        "Impossible d'insérer un hydrantPhoto dans incoming.");
  }
}
