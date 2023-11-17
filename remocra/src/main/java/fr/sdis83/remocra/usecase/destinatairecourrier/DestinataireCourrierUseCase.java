package fr.sdis83.remocra.usecase.destinatairecourrier;

import fr.sdis83.remocra.repository.DestinataireRepository;
import fr.sdis83.remocra.web.model.DestinataireModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DestinataireCourrierUseCase {
  @Autowired DestinataireRepository destinataireRepository;

  public DestinataireCourrierUseCase() {}

  public List<DestinataireModel> getAllDestinataireCourrier() {
    List<DestinataireModel> listeDestinataire = destinataireRepository.getDestinataireOrganisme();
    listeDestinataire.addAll(destinataireRepository.getDestinataireUtilisateur());
    listeDestinataire.addAll(destinataireRepository.getDestinataireContactOrganisme());
    listeDestinataire.addAll(destinataireRepository.getDestinataireContactGestionnaire());
    return listeDestinataire;
  }
}
