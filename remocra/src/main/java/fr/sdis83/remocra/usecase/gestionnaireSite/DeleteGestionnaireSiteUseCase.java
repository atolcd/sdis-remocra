package fr.sdis83.remocra.usecase.gestionnaireSite;

import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.repository.GestionnaireSiteRepository;
import fr.sdis83.remocra.repository.HydrantRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DeleteGestionnaireSiteUseCase {

  @Autowired GestionnaireSiteRepository gestionnaireSiteRepository;
  @Autowired HydrantRepository hydrantRepository;
  @Autowired ContactRepository contactRepository;

  public DeleteGestionnaireSiteUseCase() {}

  @Transactional
  public void deleteGestionnaireSite(Long idGestionnaireSite) throws Exception {
    // Suppression liens avec hydrant
    List<Long> listIdHydrant =
        gestionnaireSiteRepository.getIdHydrantWithIdGestionnaireSite(idGestionnaireSite);
    if (!listIdHydrant.isEmpty()) {
      for (Long idHydrant : listIdHydrant) {
        hydrantRepository.updateHydrantGestionnaireSite(idHydrant, null);
      }
    }
    // Suppression liens avec contact
    List<Long> listIdContact =
        gestionnaireSiteRepository.getidContactWithIdGestionnaireSite(idGestionnaireSite);
    if (!listIdContact.isEmpty()) {
      for (Long idContact : listIdContact) {
        contactRepository.updateContactGestionnaireSite(idContact, null);
      }
    }
    // Suppression Gestionnaire_Site
    gestionnaireSiteRepository.deleteGestionnaireSite(idGestionnaireSite);
  }
}
