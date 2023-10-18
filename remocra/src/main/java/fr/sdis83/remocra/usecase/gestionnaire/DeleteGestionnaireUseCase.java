package fr.sdis83.remocra.usecase.gestionnaire;

import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.HydrantRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DeleteGestionnaireUseCase {

  @Autowired GestionnaireRepository gestionnaireRepository;
  @Autowired HydrantRepository hydrantRepository;

  public DeleteGestionnaireUseCase() {}

  @Transactional
  public void deleteGestionnaire(Long idGestionnaire) throws Exception {
    // TODO réétudier pour voir si ca fait ce qu'on en attend
    List<Long> listIdHydrant =
        gestionnaireRepository.getIdHydrantWithIdGestionnaire(idGestionnaire);
    // Suppression des liens avec les hydrants, s'il y en a
    if (!listIdHydrant.isEmpty()) {
      Long newGestionnaireId = null;
      for (Long idHydrant : listIdHydrant) {
        hydrantRepository.updateHydrantGestionnaire(idHydrant, newGestionnaireId);
      }
    }
    // Suppression des roles contact, puis des contacts
    List<Long> listidContact = gestionnaireRepository.getContactGestionnaire(idGestionnaire);
    gestionnaireRepository.deleteContactRole(listidContact);
    gestionnaireRepository.deleteContactGestionnaire(idGestionnaire);

    // Suppression du gestionnaires Site
    List<Long> listIdGestionnaireSite = gestionnaireRepository.getGestionnaireSite(idGestionnaire);
    gestionnaireRepository.deleteGestionnaireSite(listIdGestionnaireSite);

    // Suppression du gestionnaire
    gestionnaireRepository.deleteGestionnaire(idGestionnaire);
  }
}
