package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.Parametre.PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParametreRepository {

  @Autowired DSLContext context;

  public ParametreRepository() {}

  @Bean
  public ParametreRepository parametreRepository(DSLContext context) {
    return new ParametreRepository(context);
  }

  ParametreRepository(DSLContext context) {
    this.context = context;
  }

  public Parametre getByCle(String cle) {
    return context
        .selectFrom(PARAMETRE)
        .where(PARAMETRE.CLE_PARAMETRE.eq(cle))
        .fetchOneInto(Parametre.class);
  }

  public void updateByKey(String key, String valeur) {
    context
        .update(PARAMETRE)
        .set(PARAMETRE.VALEUR_PARAMETRE, valeur)
        .where(PARAMETRE.CLE_PARAMETRE.eq(key))
        .execute();
  }
}
