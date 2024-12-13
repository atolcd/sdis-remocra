package fr.sdis83.remocra.usecase.synchro;

import com.google.inject.Inject;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Contact;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.ContactRole;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Gestionnaire;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantPhoto;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisiteAnomalie;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.NewHydrant;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Tournee;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.repository.DocumentsRepository;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.HydrantAnomalieRepository;
import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.repository.IncomingRepository;
import fr.sdis83.remocra.repository.NumeroUtilRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TourneeRepository;
import fr.sdis83.remocra.repository.TransactionManager;
import fr.sdis83.remocra.repository.TypeHydrantAnomalieRepository;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import fr.sdis83.remocra.usecase.visites.HydrantVisitesUseCase;
import fr.sdis83.remocra.util.GlobalConstants;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValideIncomingMobile {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Inject IncomingRepository incomingRepository;

  @Inject PeiRepository peiRepository;
  @Inject TourneeRepository tourneeRepository;
  @Inject HydrantVisitesRepository hydrantVisitesRepository;
  @Inject TypeHydrantAnomalieRepository typeHydrantAnomalieRepository;

  @Inject HydrantVisitesUseCase hydrantVisitesUseCase;

  @Inject GestionnaireRepository gestionnaireRepository;

  @Inject NumeroUtilRepository numeroUtilRepository;

  @Inject UtilisateursRepository utilisateursRepository;
  @Inject DocumentsRepository documentsRepository;
  @Inject HydrantAnomalieRepository hydrantAnomalieRepository;
  @Inject TransactionManager transactionManager;

  public static final String APPARTENANCE_GESTIONNAIRE = "GESTIONNAIRE";

  @Inject
  public ValideIncomingMobile(
      IncomingRepository incomingRepository,
      PeiRepository peiRepository,
      TourneeRepository tourneeRepository,
      HydrantVisitesRepository hydrantVisitesRepository,
      TypeHydrantAnomalieRepository typeHydrantAnomalieRepository,
      HydrantVisitesUseCase hydrantVisitesUseCase,
      GestionnaireRepository gestionnaireRepository,
      NumeroUtilRepository numeroUtilRepository,
      DocumentsRepository documentsRepository,
      HydrantAnomalieRepository hydrantAnomalieRepository,
      TransactionManager transactionManager) {
    this.incomingRepository = incomingRepository;
    this.peiRepository = peiRepository;
    this.tourneeRepository = tourneeRepository;
    this.hydrantVisitesRepository = hydrantVisitesRepository;
    this.typeHydrantAnomalieRepository = typeHydrantAnomalieRepository;
    this.hydrantVisitesUseCase = hydrantVisitesUseCase;
    this.gestionnaireRepository = gestionnaireRepository;
    this.numeroUtilRepository = numeroUtilRepository;
    this.documentsRepository = documentsRepository;
    this.hydrantAnomalieRepository = hydrantAnomalieRepository;
    this.transactionManager = transactionManager;
  }

  public void execute(Long currentUserId) throws IOException {
    transactionManager.transaction(
        configuration -> {
          List<Gestionnaire> gestionnaires = incomingRepository.getGestionnaires();
          // On s'occupe des gestionnaires avec leurs contacts en premier
          gestionGestionnaire(gestionnaires);
          gestionContact();

          // On continue par insérer les hydrants qui peuvent utiliser les gestionnaires
          gestionHydrant(currentUserId);

          // Pour toutes les tournées
          List<Tournee> tournees = incomingRepository.getTournees();
          List<Long> idsTournees =
              tournees.stream().map(Tournee::getIdTourneeRemocra).collect(Collectors.toList());
          Map<Long, List<Long>> mapHydrantByTournee =
              tourneeRepository.getHydrantsByTournee(idsTournees);
          tournees.forEach(
              tournee -> {
                try {
                  gestionHydrantVisite(currentUserId, tournee, mapHydrantByTournee);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });

          // Puis les photos
          gestionHydrantPhoto();

          // Tournée finie, on la passe à terminer et on la supprime de incoming
          logger.info(
              "Mise à jour des tournées (état à 100 et reservation à null) : {}", idsTournees);
          tourneeRepository.setTourneesFinies(idsTournees);

          logger.info("Suppression des données dans le schéma incoming");

          logger.info("Suppression des gestionnaire");
          incomingRepository.deleteGestionnaire(
              gestionnaires.stream()
                  .map(Gestionnaire::getIdGestionnaire)
                  .collect(Collectors.toList()));

          logger.info("Suppression des tournées");
          incomingRepository.deleteTournee(
              tournees.stream().map(Tournee::getIdTourneeRemocra).collect(Collectors.toList()));
        });
  }

  private void gestionHydrantPhoto() {
    List<HydrantPhoto> hydrantPhotos = incomingRepository.getListHydrantPhoto();
    for (HydrantPhoto photo : hydrantPhotos) {
      // Le code est le nom du dossier
      String path = photo.getPathHydrantPhoto();
      String code = path.split("/")[path.split("/").length - 1];
      if (code == null) {
        logger.error("Impossible de créer le document car le code est nul.");
        continue;
      }
      logger.info(
          "CREATION DOCUMENT : "
              + photo.getNomHydrantPhoto()
              + " (idHydrant : "
              + photo.getIdHydrantHydrantPhoto()
              + ")");

      Long idDocument =
          documentsRepository.insertDocumentTypeHydrant(
              path, code, photo.getDateHydrantPhoto(), photo.getNomHydrantPhoto());

      if (idDocument != null) {
        documentsRepository.insertHydrantDocument(photo.getIdHydrantHydrantPhoto(), idDocument);
      }

      logger.info("Suppression des photos");
      incomingRepository.deleteHydrantPhoto(
          hydrantPhotos.stream().map(HydrantPhoto::getIdHydrantPhoto).collect(Collectors.toList()));
    }
  }

  private void gestionHydrant(Long currentUserId) {
    List<Gestionnaire> gestionnaires = incomingRepository.getGestionnaires();

    // On intègre en premier les nouveaux hydrants
    List<NewHydrant> listNewHydrant = incomingRepository.getIncomingNewHydrant();

    for (NewHydrant newHydrant : listNewHydrant) {
      Optional<Gestionnaire> gestionnaireHydrant =
          gestionnaires.stream()
              .filter(it -> it.getIdGestionnaire().equals(newHydrant.getIdGestionnaire()))
              .findFirst();
      Long idGestionnaire = null;
      if (gestionnaireHydrant.isPresent()) {
        idGestionnaire = gestionnaireHydrant.get().getIdGestionnaireRemocra();
      } else if (newHydrant.getIdGestionnaireRemocra() != null) {
        idGestionnaire = newHydrant.getIdGestionnaireRemocra();
      }
      Hydrant hydrantTemp =
          new Hydrant(
              null,
              null,
              null,
              null,
              newHydrant.getCodeNewHydrant(),
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              newHydrant.getGeometrie(),
              null,
              null,
              null,
              newHydrant.getObservationNewHydrant(),
              1,
              newHydrant.getVoieNewHydrant(),
              null,
              newHydrant.getIdCommune(),
              null,
              newHydrant.getIdNature(),
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              currentUserId,
              newHydrant.getIdNaturedeci(),
              null,
              null,
              null,
              idGestionnaire,
              null,
              null,
              null,
              Instant.now(),
              null,
              null,
              GlobalConstants.AuteurModificationFlag.MOBILE.getAuteurModificationFlag(),
              null);

      numeroUtilRepository.setCodeZoneSpecAndNumeros(hydrantTemp, newHydrant.getCodeNewHydrant());

      Long idHydrant = peiRepository.insertHydrant(hydrantTemp, newHydrant.getGeometrie());

      if (idHydrant == null) {
        logger.error("Impossible de créer le point d'eau " + newHydrant.getIdNewHydrant());
        continue;
      }

      if (newHydrant
          .getCodeNewHydrant()
          .equalsIgnoreCase(GlobalConstants.TypeHydrant.PENA.getCode())) {
        int result = peiRepository.insertPena(idHydrant);

        if (result != 1) {
          // On supprime l'hydrant pour éviter les hydrants à moitié créer
          peiRepository.deleteHydrant(idHydrant);
          logger.error("Impossible de créer le hydrant_pena " + newHydrant.getIdNewHydrant());
          continue;
        }

      } else {
        // Sinon c'est un PIBI
        int result = peiRepository.insertPibi(idHydrant);

        if (result != 1) {
          // On supprime l'hydrant pour éviter les hydrants à moitié créer
          peiRepository.deleteHydrant(idHydrant);
          logger.error("Impossible de créer le hydrant_pibi " + newHydrant.getIdNewHydrant());
          continue;
        }
      }

      incomingRepository.deleteNewHydrant(newHydrant.getIdNewHydrant());
    }
  }

  private void gestionGestionnaire(List<Gestionnaire> gestionnaires) {
    for (Gestionnaire gestionnaire : gestionnaires) {
      if (gestionnaire.getIdGestionnaireRemocra() == null) {
        // C'est une création
        logger.info(
            "CREATION GESTIONNAIRE : "
                + gestionnaire.getCodeGestionnaire()
                + ", "
                + gestionnaire.getNomGestionnaire());
        Long idGestionnaireCree =
            gestionnaireRepository.insertGestionnaire(
                new fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire(
                    null,
                    true,
                    gestionnaire.getCodeGestionnaire(),
                    gestionnaire.getNomGestionnaire()));

        // on update le gestionnaire dans incoming
        incomingRepository.updateIdRemocraGestionnaire(
            idGestionnaireCree, gestionnaire.getIdGestionnaire());

      } else {
        // C'est un update
        logger.info(
            "UPDATE GESTIONNAIRE : "
                + gestionnaire.getCodeGestionnaire()
                + ", "
                + gestionnaire.getNomGestionnaire());
        gestionnaireRepository.updateGestionnaire(
            new fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire(
                gestionnaire.getIdGestionnaireRemocra(),
                true,
                gestionnaire.getCodeGestionnaire(),
                gestionnaire.getNomGestionnaire()));
      }
    }
  }

  private void gestionContact() {
    List<Gestionnaire> gestionnaires = incomingRepository.getGestionnaires();
    List<Contact> contacts = incomingRepository.getContacts();
    List<ContactRole> contactWithRoles = incomingRepository.getContactsRoles();

    Map<Long, List<Long>> mapRolesByContact =
        gestionnaireRepository.getRolesByContact(
            contacts.stream().map(Contact::getIdContactRemocra).collect(Collectors.toList()));

    for (Contact contact : contacts) {
      Long idAppartenance =
          gestionnaires.stream()
              .filter(gest -> gest.getIdGestionnaire().equals(contact.getIdGestionnaire()))
              .findFirst()
              .get()
              .getIdGestionnaireRemocra();

      List<Long> rolesRecuperes =
          contactWithRoles.stream().map(ContactRole::getIdRole).collect(Collectors.toList());

      if (contact.getIdContactRemocra() == null) {
        // C'est une création

        logger.info(
            "CREATION CONTACT : "
                + contact.getNomContact()
                + ", idGestionnaire = "
                + idAppartenance);
        Long idContact =
            gestionnaireRepository.insertContact(
                new fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact(
                    null,
                    APPARTENANCE_GESTIONNAIRE,
                    idAppartenance.toString(), // L'id est stocké en string
                    contact.getFonctionContact(),
                    ensureData(contact.getCiviliteContact()),
                    ensureData(contact.getNomContact()),
                    ensureData(contact.getPrenomContact()),
                    contact.getNumeroVoieContact(),
                    contact.getSuffixeVoieContact(),
                    contact.getLieuDitContact(),
                    ensureData(contact.getVoieContact()),
                    ensureData(contact.getCodePostalContact()),
                    ensureData(contact.getVilleContact()),
                    ensureData(contact.getPaysContact()),
                    contact.getTelephoneContact(),
                    ensureData(contact.getEmailContact()),
                    null));
        // On insert les rôles
        rolesRecuperes.forEach(
            idRole -> gestionnaireRepository.insertContactRole(idContact, idRole));

      } else {
        // C'est un update
        Long idGestionnaireSite =
            gestionnaireRepository.getGestionnaireSiteContact(contact.getIdContactRemocra());
        logger.info(
            "UPDATE CONTACT : " + contact.getNomContact() + ", idGestionnaire = " + idAppartenance);
        gestionnaireRepository.updateContact(
            new fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact(
                contact.getIdContactRemocra(),
                APPARTENANCE_GESTIONNAIRE,
                idAppartenance.toString(), // L'id est stocké en string
                contact.getFonctionContact(),
                contact.getCiviliteContact(),
                contact.getNomContact(),
                contact.getPrenomContact(),
                contact.getNumeroVoieContact(),
                contact.getSuffixeVoieContact(),
                contact.getLieuDitContact(),
                contact.getVoieContact(),
                contact.getCodePostalContact(),
                contact.getVilleContact(),
                contact.getPaysContact(),
                contact.getTelephoneContact(),
                contact.getEmailContact(),
                idGestionnaireSite));

        // On gère aussi les rôles
        List<Long> rolesDansRemocra = mapRolesByContact.get(contact.getIdContactRemocra());

        if (rolesRecuperes.size() == rolesDansRemocra.size()
            && new HashSet<>(rolesDansRemocra).containsAll(rolesRecuperes)) {
          // Les rôles n'ont pas changés donc on y touche pas
        } else {
          List<Long> rolesAEnlever = rolesDansRemocra;
          rolesAEnlever.removeAll(rolesRecuperes);

          rolesAEnlever.forEach(
              idRole ->
                  gestionnaireRepository.deleteContactRole(contact.getIdContactRemocra(), idRole));

          List<Long> rolesAAjouter = rolesRecuperes;
          rolesAAjouter.removeAll(rolesDansRemocra);

          rolesAEnlever.forEach(
              idRole ->
                  gestionnaireRepository.insertContactRole(contact.getIdContactRemocra(), idRole));
        }
      }
    }

    // On supprime les données des tables gestionnaire / contact et contact_role de incoming
    List<UUID> contactsId =
        contacts.stream().map(Contact::getIdContact).collect(Collectors.toList());
    logger.info("Suppression des contactRole");
    incomingRepository.deleteContactRole(contactsId);

    logger.info("Suppression des contact");
    incomingRepository.deleteContact(contactsId);
  }

  private String ensureData(String data) {
    return data == null ? "" : data;
  }

  private void gestionHydrantVisite(
      Long currentUserId, Tournee tournee, Map<Long, List<Long>> mapHydrantByTournee)
      throws IOException {
    List<Long> listIdHydrant = mapHydrantByTournee.get(tournee.getIdTourneeRemocra());

    // Récupérer les hydrants visites
    List<HydrantVisite> listHydrantVisite = incomingRepository.getHydrantVisite(listIdHydrant);

    // Les anomalies des visites
    List<HydrantVisiteAnomalie> listeHydrantAnomalie =
        incomingRepository.getHydrantVisiteAnomalie(
            listHydrantVisite.stream()
                .map(HydrantVisite::getIdHydrantVisite)
                .collect(Collectors.toList()));

    logger.info(
        "Insertion des visites pour la tournée {} (id: {})",
        tournee.getNomTournee(),
        tournee.getIdTourneeRemocra());
    for (HydrantVisite hydrantVisite : listHydrantVisite) {
      if (hydrantVisitesRepository.checkVisiteMemeHeure(
          hydrantVisite.getIdHydrant(), hydrantVisite.getDateHydrantVisite())) {
        continue;
      }

      List<Long> anomaliesId;
      if (!hydrantVisite.getHasAnomalieChanges()) {
        // Les anomalies n'ont pas changé, on va donc chercher les dernières en base
        anomaliesId =
            hydrantAnomalieRepository.getAnomalieIdNonSysteme(hydrantVisite.getIdHydrant());
      } else {
        anomaliesId =
            listeHydrantAnomalie.stream()
                .filter(it -> it.getIdHydrantVisite().equals(hydrantVisite.getIdHydrantVisite()))
                .map(it -> it.getIdAnomalie())
                .collect(Collectors.toList());
      }

      String anomaliesToString = null;
      if (!anomaliesId.isEmpty()) {
        // On construit la chaîne "[ID_ANOMALIE_1,ID_ANOMALIE_2]"
        anomaliesToString =
            "[" + anomaliesId.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
      }

      logger.info("CREATION VISITE : idHydrant {}", hydrantVisite.getIdHydrant());
      hydrantVisitesRepository.addVisite(
          new fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite(
              null,
              hydrantVisite.getIdHydrant(),
              hydrantVisite.getDateHydrantVisite(),
              hydrantVisite.getIdTypeHydrantSaisie(),
              hydrantVisite.getCtrlDebitPression(),
              hydrantVisite.getAgent1HydrantVisite(),
              hydrantVisite.getAgent2HydrantVisite(),
              hydrantVisite.getDebitHydrantVisite(),
              null,
              hydrantVisite.getPressionHydrantVisite(),
              hydrantVisite.getPressionDynHydrantVisite(),
              null,
              anomaliesToString,
              hydrantVisite.getObservationsHydrantVisite(),
              currentUserId,
              null,
              GlobalConstants.AuteurModificationFlag.MOBILE.getAuteurModificationFlag(),
              null,
              null),
          utilisateursRepository.getOrganisme(currentUserId));

      // On gère les anomalies
      logger.info("Ajout des anomalies pour la visite {}", hydrantVisite.getIdHydrant());
      hydrantVisitesUseCase.launchTriggerAnomalies(hydrantVisite.getIdHydrant());
      logger.info("Fin d'ajout des anomalies pour la visite {}", hydrantVisite.getIdHydrant());
    }

    // On a fini, on supprime les hydrants visite, les tournees
    List<UUID> visitesId =
        listHydrantVisite.stream()
            .map(HydrantVisite::getIdHydrantVisite)
            .collect(Collectors.toList());
    logger.info("Suppression des anomalies");
    incomingRepository.deleteHydrantVisiteAnomalie(visitesId);
    logger.info("Suppression des visites");
    incomingRepository.deleteHydrantVisite(visitesId);

    logger.info(
        "Fin d'intégration des visites pour la tournée {} (id: {})",
        tournee.getNomTournee(),
        tournee.getIdTourneeRemocra());
  }
}
