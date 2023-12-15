package fr.sdis83.remocra.usecase.parametre;

import fr.sdis83.remocra.data.CleValeurClasseData;
import fr.sdis83.remocra.data.ParametreData;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.repository.ParametreRepository;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

@Singleton
@Configuration
public class ParametreDataProvider implements Provider<ParametreData> {
  private ParametreData parametreData = null;

  private final Logger logger = Logger.getLogger(getClass());

  @Inject private ParamConfRepository paramConfRepository;

  @Inject private ParametreRepository parametreRepository;

  public void reloadParametres() {
    parametreData = getParametreData();
  }

  private ParametreData getParametreData() {
    Collection<CleValeurClasseData> listParamConf = paramConfRepository.getParametres();
    Collection<Parametre> listParametres = parametreRepository.getParametres();

    return new ParametreData(listParamConf, listParametres);
  }

  @Override
  public ParametreData get() {
    if (parametreData == null) {
      logger.info("Mise en cache des paramètres");
      parametreData = getParametreData();
    }
    return parametreData;
  }
}
