package fr.sdis83.remocra.usecase.synchro;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.repository.IncomingRepository;
import fr.sdis83.remocra.repository.TypeDroitRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureDeciRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.web.model.mobilemodel.HydrantVisiteModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TypeHydrantSaisieRepository;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

public class SynchroUseCase {
    @Inject
    IncomingRepository incomingRepository;

    @Inject
    TypeDroitRepository typeDroitRepository;
    @Inject
    PeiRepository peiRepository;

    @Inject
    TypeHydrantSaisieRepository typeHydrantSaisieRepository;

    @Inject
    TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository;

    @Inject
    TypeHydrantNatureRepository typeHydrantNatureRepository;


    @Inject
    GestionnaireRepository gestionnaireRepository;

    @Inject
    public SynchroUseCase(IncomingRepository incomingRepository,
                          TypeDroitRepository typeDroitRepository,
                          TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository,
                          TypeHydrantNatureRepository typeHydrantNatureRepository,
                          GestionnaireRepository gestionnaireRepository,
                          PeiRepository peiRepository,
                          TypeHydrantSaisieRepository typeHydrantSaisieRepository) {
        this.incomingRepository = incomingRepository;
        this.typeDroitRepository = typeDroitRepository;
        this.typeHydrantNatureDeciRepository = typeHydrantNatureDeciRepository;
        this.typeHydrantNatureRepository = typeHydrantNatureRepository;
        this.gestionnaireRepository = gestionnaireRepository;
        this.typeHydrantSaisieRepository = typeHydrantSaisieRepository;
        this.peiRepository = peiRepository;
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


    /**
     * Permet en fonction de s'il s'agit d'un PIBI ou d'un PENA de l'ajouter en base
     *
     */
    public Response insertHydrant(UUID idHydrant,
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

        // Puis, on finit en faisant un insert
        String error = "Impossible d'insérer l'hydrant " + idHydrant + " dans incoming.";
        try {
            int result = incomingRepository.insertPei(
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
                    error
            );
        } catch(Exception e) {
            return Response.serverError().entity(error).build();
        }
    }


    public Response insertGestionnaire(Long idRemocra, String codeGestionnaire, String nomGestionnaire, UUID idGestionnaire) {
        String erreur = checkErrorGestionnaire(idRemocra);
        if (erreur != null) {
            return Response.serverError().entity(erreur).build();
        }

        String error = "Impossible d'insérer le gestionnaire "
                + idGestionnaire + " dans incoming.";
        try {
            int result = incomingRepository.insertGestionnaire(idRemocra, codeGestionnaire, nomGestionnaire, idGestionnaire);

            return gestionResult(result,
                    "Le gestionnaire " + idGestionnaire + " a été inséré dans incoming.",
                    "Le gestionnaire " + idGestionnaire+ " est déjà dans le schéma incoming.",
                    error
            );

        } catch (Exception e) {
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
            return Response.serverError().entity(erreur).build();
        }

        String error = "Impossible d'insérer le contact " + idContact + " dans incoming.";
        try {
            int result = incomingRepository.insertContact(contactModel, idContact, idGestionnaire);
            return gestionResult(result,
                    "Le contact " + idContact + " a été inséré dans incoming.",
                    "Le contact " + idContact+ " est déjà dans le schéma incoming.",
                    error
            );
        } catch (Exception e) {
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
            return "Le gestionnaire " + idGestionnaire + " associé au contact " +
                   idContact + " n'existe pas.";
        }
        return null;
    }

    public Response insertContactRole(UUID idContact, Long idRole) {
        String erreur = checkErrorContactRole(idContact, idRole);
        if (erreur != null) {
            return Response.serverError().entity(erreur).build();
        }

        String error = "Impossible d'insérer le contact_role "
                + idContact + " - " + idRole
                + " dans incoming.";

        try {
            int result = incomingRepository.insertContactRole(idContact, idRole);
            return gestionResult(result,
                    "Le contactRole " + idContact + " - " + idRole +" a été inséré dans incoming.",
                    "Le contactRole "+ idContact + " - " + idRole +" est déjà dans le schéma incoming.",
                    error
            );

        } catch (Exception e) {
            return Response.serverError().entity(error).build();
        }
    }

    /**
     * Permet d'insérer une visite dans le schéma incoming
     *
     * @param hydrantVisiteModel
     * @return
     */
    public Response insertVisite(HydrantVisiteModel hydrantVisiteModel) {
        // Si les données ne sont pas valides, on retourne l'erreur concernée
        String erreur = checkError(hydrantVisiteModel);
        if (erreur != null) {
            return Response.serverError().entity(erreur).build();
        }

        Response serverErrorBuild = Response.serverError().entity("Impossible de créer la visite pour l'hydrant "
                + hydrantVisiteModel.idHydrant() + " dans incoming.").build();
        try {
            int result = incomingRepository.insertVisite(hydrantVisiteModel);
            switch(result) {
                case 1:
                    return Response.ok().entity("La visite " + hydrantVisiteModel.idHydrantVisite()
                            + " a été inséré dans incoming.").build();
                case 0:
                    return Response.ok().entity("La visite " + hydrantVisiteModel.idHydrantVisite()
                            + " est déjà dans le schéma incoming.").build();
                default:
                    return serverErrorBuild;
            }
        } catch (Exception e) {
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
            return "Le contact " + idContact + " associé au rôle " +
                    idRole + " n'existe pas.";
        }
        return null;
    }

    private Response gestionResult(int result, String inserer, String dejaEnBase, String error) {
        switch(result) {
            case 1:
                return Response.ok().entity(inserer).build();
            case 0:
                return Response.ok().entity(dejaEnBase).build();
            default:
                return Response.serverError().entity(error).build();
        }
    }
}
