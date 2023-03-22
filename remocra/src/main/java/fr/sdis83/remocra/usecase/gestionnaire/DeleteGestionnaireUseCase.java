package fr.sdis83.remocra.usecase.gestionnaire;

import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.HydrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class DeleteGestionnaireUseCase {

    @Autowired
    GestionnaireRepository gestionnaireRepository;
    @Autowired
    HydrantRepository hydrantRepository;

    public DeleteGestionnaireUseCase(){
    }

    @Transactional
    public void execute(Long idGestionnaire) throws Exception {
        // On vérifie d'abord que le gestionnaire n'est pas utilisé pour un autre point d'eau
        List<String> listeNumerosHydrant = gestionnaireRepository.getHydrantWithIdGestionnaire(idGestionnaire);

        if(!listeNumerosHydrant.isEmpty()) {
            throw new IllegalArgumentException("Le gestionnaire que vous essayez de supprimer " +
                    "est utilisé sur d'autres PEI : "+ listeNumerosHydrant);
        }

        // Si tous est bon, on récupère les id des contacts du gestionnaire
        List<Long> listIdContact = gestionnaireRepository.getContactGestionnaire(idGestionnaire);

        // Puis, on supprime les liens rôle - contact
        gestionnaireRepository.deleteContactRole(listIdContact);

        // Puis, les contacts
        gestionnaireRepository.deleteContactGestionnaire(idGestionnaire);

        // Puis, le site
        List<Long> listIdGestionnaireSite = gestionnaireRepository.getGestionnaireSite(idGestionnaire);
        gestionnaireRepository.deleteSite(listIdGestionnaireSite);

        // Puis, le gestionnaire site
        gestionnaireRepository.deleteGestionnaireSite(idGestionnaire);

        // Puis le gestionnaire lui-même
        gestionnaireRepository.deleteGestionnaire(idGestionnaire);

    }

    @Transactional
    public void deleteGestionnaire(Long idGestionnaire) throws Exception{
        List<Long> listIdHydrant = gestionnaireRepository.getIdHydrantWithIdGestionnaire(idGestionnaire);
        // Suppression des liens avec les hydrants, s'il y en a
        if(!listIdHydrant.isEmpty()){
            Long newGestionnaireId = null;
            for(Long idHydrant : listIdHydrant){
                hydrantRepository.updateHydrantGestionnaire(idHydrant, newGestionnaireId);
            }
        }
        // Suppression des roles contact, puis des contacts
        List<Long> listidContact = gestionnaireRepository.getContactGestionnaire(idGestionnaire);
        gestionnaireRepository.deleteContactRole(listidContact);
        gestionnaireRepository.deleteContactGestionnaire(idGestionnaire);

        // Suppression du gestionnaires Site
        List<Long> listIdGestionnaireSite = gestionnaireRepository.getGestionnaireSite(idGestionnaire);
        gestionnaireRepository.deleteSite(listIdGestionnaireSite);

        // Suppression du gestionnaire
        gestionnaireRepository.deleteGestionnaire(idGestionnaire);
    }

}
