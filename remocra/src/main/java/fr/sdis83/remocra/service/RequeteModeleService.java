package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.RequeteModele;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequeteModeleService extends AbstractService<RequeteModele> {

  public RequeteModeleService() {
    super(RequeteModele.class);
  }
}
