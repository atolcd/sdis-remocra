package fr.sdis83.remocra.usecase.destinatairecourrier;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.repository.DestinataireRepository;
import fr.sdis83.remocra.web.model.DestinataireModel;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DestinataireCourrierUseCase {
  @Autowired DestinataireRepository destinataireRepository;

  public DestinataireCourrierUseCase() {}

  public List<DestinataireModel> getAllDestinataireCourrier(
      boolean useZC, Geometry geometryOrganismeCurrentUser) {
    List<Long> listeIdOrganismeByZC = Collections.EMPTY_LIST;
    if (useZC) {
      listeIdOrganismeByZC =
          destinataireRepository.getAllOrganismeIdZCByGeometry(geometryOrganismeCurrentUser);
    }
    List<DestinataireModel> listeDestinataire =
        destinataireRepository.getDestinataireOrganisme(listeIdOrganismeByZC);
    listeDestinataire.addAll(
        destinataireRepository.getDestinataireUtilisateur(listeIdOrganismeByZC));
    listeDestinataire.addAll(
        destinataireRepository.getDestinataireContactOrganisme(listeIdOrganismeByZC));
    listeDestinataire.addAll(destinataireRepository.getDestinataireContactGestionnaire());
    return listeDestinataire;
  }
}
