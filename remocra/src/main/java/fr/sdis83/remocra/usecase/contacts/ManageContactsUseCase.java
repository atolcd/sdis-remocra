package fr.sdis83.remocra.usecase.contacts;

import fr.sdis83.remocra.repository.ContactRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManageContactsUseCase {
  @Autowired ContactRepository contactRepository;

  public ManageContactsUseCase() {}

  public List<ContactGestionnaireSite> fetchContactsGestionnaireSiteByGestionnaireId(
      Long idGestionnaire) {
    return contactRepository.getContactGestionnaireSiteByGestionnaireId(idGestionnaire);
  }
}
