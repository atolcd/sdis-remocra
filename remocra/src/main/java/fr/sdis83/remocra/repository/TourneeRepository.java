package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_TOURNEES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TOURNEE;

@Configuration

public class TourneeRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired
  DSLContext context;

  @Autowired
  JpaTransactionManager transactionManager;

  public TourneeRepository() {

  }

  @Bean
  public TourneeRepository TourneeRepository(DSLContext context) {
    return new TourneeRepository(context);
  }

  TourneeRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Renvoie tous les hydrants composant une tournée
   * @param id Identifiant de la tournée
   */
  public List<Hydrant> getHydrants(Long id) {
    return context.select(HYDRANT.fields())
      .from(HYDRANT)
      .join(HYDRANT_TOURNEES).on(HYDRANT_TOURNEES.HYDRANT.eq(HYDRANT.ID))
      .join(TOURNEE).on(TOURNEE.ID.eq(HYDRANT_TOURNEES.TOURNEES))
      .where(TOURNEE.ID.eq(id))
      .fetchInto(Hydrant.class);
  }

  /**
   * Renvoie le nombre d'hydrants composant une tournée
   * @param id L'id de la tournée
   * @return
   */
  public Integer countHydrants(Long id) {
    return context.selectCount()
      .from(HYDRANT_TOURNEES)
      .where(HYDRANT_TOURNEES.TOURNEES.eq(id))
      .fetchOneInto(Integer.class);
  }


}
