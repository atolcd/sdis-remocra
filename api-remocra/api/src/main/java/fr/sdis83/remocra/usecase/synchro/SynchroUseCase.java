package fr.sdis83.remocra.usecase.synchro;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.repository.IncomingRepository;
import fr.sdis83.remocra.repository.TypeDroitRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureDeciRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

public class SynchroUseCase {
    @Inject
    IncomingRepository incomingRepository;

    @Inject
    TypeDroitRepository typeDroitRepository;

    @Inject
    TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository;

    @Inject
    TypeHydrantNatureRepository typeHydrantNatureRepository;


    @Inject
    public SynchroUseCase(IncomingRepository incomingRepository,
                          TypeDroitRepository typeDroitRepository,
                          TypeHydrantNatureDeciRepository typeHydrantNatureDeciRepository,
                          TypeHydrantNatureRepository typeHydrantNatureRepository) {
        this.incomingRepository = incomingRepository;
        this.typeDroitRepository = typeDroitRepository;
        this.typeHydrantNatureDeciRepository = typeHydrantNatureDeciRepository;
        this.typeHydrantNatureRepository = typeHydrantNatureRepository;
    }

    /**
     * Permet de spécifier si l'utilisateur peut créer un hydrant
     * @return
     */
    public boolean getDroit(Long currentUserId) {
        // On vérifie que l'utilisateur a bien les droits pour créer des points d'eau depuis l'appli mobile
        List<String> droits = typeDroitRepository.getDroitsUtilisateurMobile(currentUserId);

        if (!droits.contains(TypeDroitRepository.TypeDroitsPourMobile.CREATION_PEI_MOBILE.codeDroitMobile)) {
            return false;
        }
        return true;
    }


    /**
     * Permet en fonction de s'il s'agit d'un PIBI ou d'un PENA de l'ajouter en base
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
