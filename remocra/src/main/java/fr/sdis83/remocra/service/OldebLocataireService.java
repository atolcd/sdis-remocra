package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.OldebLocataire;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OldebLocataireService extends AbstractService<OldebLocataire> {

  public OldebLocataireService() {
    super(OldebLocataire.class);
  }
}
