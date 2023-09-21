package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.OldebPropriete;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OldebProprieteService extends AbstractService<OldebPropriete> {

  public OldebProprieteService() {
    super(OldebPropriete.class);
  }
}
