package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.REPERTOIRE_LIEU;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepertoireLieuRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;


  public RepertoireLieuRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public RepertoireLieuRepository repertoireLieuRepository(DSLContext context) {
    return new RepertoireLieuRepository(context);
  }

  RepertoireLieuRepository(DSLContext context) {
    this.context = context;
  }

  public List<RepertoireLieu> getAll(List<ItemFilter> itemFilters) {
    List<RepertoireLieu> l = null;
    l = context.select().from(REPERTOIRE_LIEU).fetchInto(RepertoireLieu.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(REPERTOIRE_LIEU));
  }

}
