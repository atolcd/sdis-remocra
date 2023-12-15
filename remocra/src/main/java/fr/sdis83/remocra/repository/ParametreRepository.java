package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.Parametre.PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import java.util.Collection;
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

  /**
   * @deprecated ne plus utiliser, le ParametreProvider arrive !
   * @param cle
   * @return
   */
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

  /**
   * Retourne la liste de *tous* les paramètres. <br>
   * <b>Ne pas utiliser en dehors du provider de paramètres</b>
   *
   * @return Collection<Parametre>
   */
  public Collection<Parametre> getParametres() {
    return context.selectFrom(PARAMETRE).fetchInto(Parametre.class);
  }
}
