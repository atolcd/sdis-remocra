package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalieNature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeHydrantAnomalieNatureService extends AbstractService<TypeHydrantAnomalieNature> {

  // private final Logger logger = Logger.getLogger(getClass());

  public TypeHydrantAnomalieNatureService() {
    super(TypeHydrantAnomalieNature.class);
  }

  @Bean
  public TypeHydrantAnomalieNatureService typeHydrantAnomalieNatureService() {
    return new TypeHydrantAnomalieNatureService();
  }

  @Override
  protected void beforeDelete(TypeHydrantAnomalieNature attached) {
    attached.getSaisies().clear();
  }
}
