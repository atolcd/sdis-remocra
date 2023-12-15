package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;

import fr.sdis83.remocra.data.CleValeurClasseData;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ParamConf;
import java.util.Collection;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Singleton
public class ParamConfRepository {
  @Autowired DSLContext context;

  public ParamConfRepository() {}

  @Bean
  public ParamConfRepository paramConfRepository(DSLContext context) {
    return new ParamConfRepository(context);
  }

  // Clés possibles
  public static final String DOSSIER_ROOT = "1_DOSSIER_ROOT";
  public static final String DOSSIER_INTEGRATION = "2_DOSSIER_INTEGRATION";
  public static final String DOSSIER_SAUVEGARDE_DOCUMENTS = "3_DOSSIER_SAUVEGARDE_DOCUMENTS";

  ParamConfRepository(DSLContext context) {
    this.context = context;
  }

  public ParamConf getByCle(String cle) {
    return context
        .selectFrom(PARAM_CONF)
        .where(PARAM_CONF.CLE.eq(cle))
        .fetchOneInto(ParamConf.class);
  }

  public Collection<CleValeurClasseData> getParametres() {
    return context
        .select(PARAM_CONF.CLE, PARAM_CONF.VALEUR)
        .from(PARAM_CONF)
        .fetchInto(CleValeurClasseData.class);
  }

  public void updateParamConf(String cle, String valeur) {
    context
        .update(PARAM_CONF)
        .set(PARAM_CONF.VALEUR, valeur)
        .where(PARAM_CONF.CLE.eq(cle))
        .execute();
  }
}
