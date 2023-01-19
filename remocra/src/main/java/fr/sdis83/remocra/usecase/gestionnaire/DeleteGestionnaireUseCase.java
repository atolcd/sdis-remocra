package fr.sdis83.remocra.usecase.gestionnaire;

import fr.sdis83.remocra.repository.GestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class DeleteGestionnaireUseCase {

    @Autowired
    GestionnaireRepository gestionnaireRepository;

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
        gestionnaireRepository.getContactGestionnaire(idGestionnaire);

        // Puis, le site
        gestionnaireRepository.deleteSite(idGestionnaire);

        // Puis, le gestionnaire site
        gestionnaireRepository.deleteGestionnaireSite(idGestionnaire);

        // Puis le gestionnaire lui-même
        gestionnaireRepository.deleteGestionnaire(idGestionnaire);

    }

}
