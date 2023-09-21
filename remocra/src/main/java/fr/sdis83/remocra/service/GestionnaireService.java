package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Gestionnaire;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GestionnaireService extends AbstractService<Gestionnaire> {

  public GestionnaireService() {
    super(Gestionnaire.class);
  }
}
