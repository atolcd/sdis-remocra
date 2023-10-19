package fr.sdis83.remocra.usecase.gestionnaire;

import fr.sdis83.remocra.repository.GestionnaireRepository;
import fr.sdis83.remocra.repository.GestionnaireSiteRepository;
import fr.sdis83.remocra.repository.HydrantRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DeleteGestionnaireUseCase {

  @Autowired GestionnaireRepository gestionnaireRepository;
  @Autowired GestionnaireSiteRepository gestionnaireSiteRepository;
  @Autowired HydrantRepository hydrantRepository;

  public DeleteGestionnaireUseCase() {}

  @Transactional
  public void deleteGestionnaire(Long idGestionnaire) throws Exception {
    List<Long> listIdHydrant =
        gestionnaireRepository.getIdHydrantWithIdGestionnaire(idGestionnaire);
    // Suppression des liens avec les hydrants, s'il y en a
    if (!listIdHydrant.isEmpty()) {
      for (Long idHydrant : listIdHydrant) {
        hydrantRepository.updateHydrantGestionnaire(idHydrant, null);
      }
    }
    // Suppression des roles contact, puis des contacts
    List<Long> listidContact = gestionnaireRepository.getContactGestionnaire(idGestionnaire);
    gestionnaireRepository.deleteContactRole(listidContact);
    gestionnaireRepository.deleteContactGestionnaire(idGestionnaire);

    // Suppression des liens avec les gestionnaires_Site, s'il y en a
    List<Long> listIdGestionnaireSite =
        gestionnaireSiteRepository.getGestionnaireSiteByGestionnaireId(idGestionnaire);
    if (!listIdGestionnaireSite.isEmpty()) {
      for (Long idGestionnaireSite : listIdGestionnaireSite) {
        gestionnaireSiteRepository.setGestionnaireIdInGestionnaireSite(idGestionnaireSite, null);
      }
    }

    // Suppression du gestionnaire
    gestionnaireRepository.deleteGestionnaire(idGestionnaire);
  }
}
