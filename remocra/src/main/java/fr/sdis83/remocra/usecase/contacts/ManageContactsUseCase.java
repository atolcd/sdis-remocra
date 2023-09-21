package fr.sdis83.remocra.usecase.contacts;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.repository.ContactRepository;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ManageContactsUseCase {
  @Autowired GestionnaireRepository gestionnaireRepository;
  @Autowired ContactRepository contactRepository;
  @Autowired DSLContext context;

  public ManageContactsUseCase() {}

  @Transactional
  public List<Contact> fetchContactsDataByGestionnaireId(Long idGestionnaire) {
    List<Long> listeContacts = gestionnaireRepository.getContactGestionnaire(idGestionnaire);
    List<Contact> listeContactsInfos = new ArrayList<>();
    for (Long id : listeContacts) {
      Contact contact = contactRepository.getContact(id);
      listeContactsInfos.add(contact);
    }
    return listeContactsInfos;
  }
}
