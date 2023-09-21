package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.TypeOldebResidence;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeOldebResidenceService extends AbstractService<TypeOldebResidence> {

  public TypeOldebResidenceService() {
    super(TypeOldebResidence.class);
  }
}
