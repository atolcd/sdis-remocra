package fr.sdis83.remocra.usecase.synchro;

import com.google.inject.Inject;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.repository.IncomingRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TourneeRepository;
import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.NumeroUtilRepository;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import fr.sdis83.remocra.usecase.visites.HydrantVisitesUseCase;
import fr.sdis83.remocra.repository.TypeHydrantAnomalieRepository;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.NewHydrant;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Tournee;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisiteAnomalie;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Gestionnaire;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.Contact;
import fr.sdis83.remocra.db.model.incoming.tables.pojos.ContactRole;
import fr.sdis83.remocra.util.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValideIncomingMobile {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    IncomingRepository incomingRepository;

    @Inject
    PeiRepository peiRepository;
    @Inject
    TourneeRepository tourneeRepository;
    @Inject
    HydrantVisitesRepository hydrantVisitesRepository;
    @Inject
    TypeHydrantAnomalieRepository typeHydrantAnomalieRepository;

    @Inject
    HydrantVisitesUseCase hydrantVisitesUseCase;

    @Inject
    GestionnaireRepository gestionnaireRepository;

    @Inject
    NumeroUtilRepository numeroUtilRepository;

    @Inject
    UtilisateursRepository utilisateursRepository;

    public final static String APPARTENANCE_GESTIONNAIRE = "GESTIONNAIRE";

    @Inject
    public ValideIncomingMobile(IncomingRepository incomingRepository,
                                PeiRepository peiRepository,
                                TourneeRepository tourneeRepository,
                                HydrantVisitesRepository hydrantVisitesRepository,
                                TypeHydrantAnomalieRepository typeHydrantAnomalieRepository,
                                HydrantVisitesUseCase hydrantVisitesUseCase,
                                GestionnaireRepository gestionnaireRepository,
                                NumeroUtilRepository numeroUtilRepository) {
        this.incomingRepository = incomingRepository;
        this.peiRepository = peiRepository;
        this.tourneeRepository = tourneeRepository;
        this.hydrantVisitesRepository = hydrantVisitesRepository;
        this.typeHydrantAnomalieRepository = typeHydrantAnomalieRepository;
        this.hydrantVisitesUseCase = hydrantVisitesUseCase;
        this.gestionnaireRepository = gestionnaireRepository;
        this.numeroUtilRepository = numeroUtilRepository;
    }

    public void execute(Long currentUserId) throws IOException {
        // On s'occupe des gestionnaires avec leurs contacts en premier
        List<Gestionnaire> gestionnaires = incomingRepository.getGestionnaires();
        gestionGestionnaire(gestionnaires);
        gestionContact(gestionnaires);

        // On continue par insérer les hydrants qui peuvent utiliser les gestionnaires
        gestionHydrant(currentUserId, gestionnaires);


        // Pour toutes les tournées
        List<Tournee> tournees = incomingRepository.getTournees();
        List<Long> idsTournees = tournees.stream().map(Tournee::getIdTourneeRemocra).collect(Collectors.toList());
        Map<Long, List<Long>> mapHydrantByTournee = tourneeRepository.getHydrantsByTournee(idsTournees);
        tournees.forEach(tournee ->
                {
                    try {
                        gestionHydrantVisite(currentUserId, tournee, mapHydrantByTournee);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        // Tournée finie, on la passe à terminer et on la supprime de incoming
        tourneeRepository.setTourneesFinies(idsTournees);

        // On supprime les données des tables gestionnaire / contact et contact_role de incoming
        incomingRepository.deleteContactRole();
        incomingRepository.deleteContact();
        incomingRepository.deleteGestionnaire();

        // On a fini, on supprime les hydrants visite, les tournees
        incomingRepository.deleteHydrantVisiteAnomalie();
        incomingRepository.deleteHydrantVisite();
        incomingRepository.deleteTournee();
    }

    private void gestionHydrant(Long currentUserId, List<Gestionnaire> gestionnaires) {
        // On intègre en premier les nouveaux hydrants
        List<NewHydrant> listNewHydrant = incomingRepository.getIncomingNewHydrant();

        for (NewHydrant newHydrant : listNewHydrant) {
            Hydrant hydrantTemp =  new Hydrant(
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
                    gestionnaires.stream().filter(it -> it.getIdGestionnaire() == newHydrant.getIdGestionnaire())
                            .findFirst().get().getIdGestionnaireRemocra(),
                    null,
                    null,
                    null,
                    Instant.now(),
                    null,
                    null,
                    GlobalConstants.AuteurModificationFlag.MOBILE.getAuteurModificationFlag(),
                    null);

            numeroUtilRepository.setCodeZoneSpecAndNumeros(hydrantTemp, newHydrant.getCodeNewHydrant());

            Long idHydrant = peiRepository.insertHydrant(hydrantTemp);

            if (idHydrant == null) {
                logger.error("Impossible de créer le point d'eau " + newHydrant.getIdNewHydrant());
                continue;
            }

            if (newHydrant.getCodeNewHydrant().equalsIgnoreCase(GlobalConstants.TypeHydrant.PENA.getCode())) {
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

        for (Gestionnaire gestionnaire: gestionnaires) {
            if(gestionnaire.getIdGestionnaireRemocra() == null) {
                // C'est une création
                Long idGestionnaireCree = gestionnaireRepository.insertGestionnaire(
                        new fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire(
                               null,
                                true,
                                gestionnaire.getCodeGestionnaire(),
                                gestionnaire.getNomGestionnaire(),
                                1
                        )
                );

                // on update le gestionnaire dans incoming
                incomingRepository.updateIdRemocraGestionnaire(idGestionnaireCree, gestionnaire.getIdGestionnaire());

            } else {
                // C'est un update
                gestionnaireRepository.updateGestionnaire(
                        new fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire(
                                gestionnaire.getIdGestionnaireRemocra(),
                                true,
                                gestionnaire.getCodeGestionnaire(),
                                gestionnaire.getNomGestionnaire(),
                                null
                        )
                );

            }
        }

    }

    private void gestionContact(List<Gestionnaire> gestionnaires) {
        List<Contact> contacts = incomingRepository.getContacts();
        List<ContactRole> contactWithRoles = incomingRepository.getContactsRoles();

        Map<Long, List<Long>> mapRolesByContact = gestionnaireRepository.getRolesByContact(
                contacts.stream().map(Contact::getIdContactRemocra).collect(Collectors.toList()));

        for (Contact contact : contacts) {
            Long idAppartenance =
                    gestionnaires.stream().filter(gest -> gest.getIdGestionnaire().equals(contact.getIdGestionnaire()))
                            .findFirst().get().getIdGestionnaireRemocra();

            List<Long> rolesRecuperes = contactWithRoles.stream().map(ContactRole::getIdRole).collect(Collectors.toList());

            if (contact.getIdContactRemocra() == null) {
                // C'est une création
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
                                ensureData(contact.getEmailContact())
                        )
                );
                // On insert les rôles
                rolesRecuperes.forEach(idRole ->
                        gestionnaireRepository.insertContactRole(idContact, idRole)
                );

            } else {
                // C'est un update
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
                                contact.getEmailContact()
                        )
                );

                // On gère aussi les rôles
                List<Long> rolesDansRemocra = mapRolesByContact.get(contact.getIdContactRemocra());

                if(rolesRecuperes.size() == rolesDansRemocra.size()
                        && new HashSet<>(rolesDansRemocra).containsAll(rolesRecuperes)) {
                    // Les rôles n'ont pas changés donc on y touche pas
                } else {
                    List<Long> rolesAEnlever = rolesDansRemocra;
                    rolesAEnlever.removeAll(rolesRecuperes);

                    rolesAEnlever.forEach(idRole ->
                            gestionnaireRepository.deleteContactRole(contact.getIdContactRemocra(), idRole)
                    );

                    List<Long> rolesAAjouter = rolesRecuperes;
                    rolesAAjouter.removeAll(rolesDansRemocra);

                    rolesAEnlever.forEach(idRole ->
                            gestionnaireRepository.insertContactRole(contact.getIdContactRemocra(), idRole)
                    );

                }
            }
        }

    }

    private String ensureData(String data) {
        return data == null ? "" : data;
    }



    private void gestionHydrantVisite(Long currentUserId, Tournee tournee, Map<Long, List<Long>> mapHydrantByTournee) throws IOException {
         List<Long> listIdHydrant = mapHydrantByTournee.get(tournee.getIdTourneeRemocra());

        // Récupérer les hydrants visites
        List<HydrantVisite> listHydrantVisite = incomingRepository.getHydrantVisite(listIdHydrant);

        // Les anomalies des visites
        List<HydrantVisiteAnomalie> listeHydrantAnomalie =
                incomingRepository.getHydrantVisiteAnomalie(
                        listHydrantVisite.stream().map(HydrantVisite::getIdHydrantVisite).collect(Collectors.toList()));


        for (HydrantVisite hydrantVisite : listHydrantVisite) {
            List<String> anomaliesId = listeHydrantAnomalie.stream()
                    .filter(it -> it.getIdHydrantVisite().equals(hydrantVisite.getIdHydrantVisite())).map(it -> it.getIdAnomalie().toString())
                    .collect(Collectors.toList());

            String anomaliesToString = null;
            if (!anomaliesId.isEmpty()) {
                // On construit la chaîne "[ID_ANOMALIE_1, ID_ANOMALIE_2]"
                anomaliesToString = "[" + String.join(",", anomaliesId) + "]";
            }

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
                utilisateursRepository.getOrganisme(currentUserId)
            );

            // On gère les anomalies
            hydrantVisitesUseCase.launchTriggerAnomalies(hydrantVisite.getIdHydrant());
        }
    }

}