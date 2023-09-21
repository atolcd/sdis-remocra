package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.TypeDroit;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeDroitService extends AbstractService<TypeDroit> {

  public TypeDroitService() {
    super(TypeDroit.class);
  }
}
