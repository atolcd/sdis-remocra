package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.OGC_COUCHE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OgcCoucheRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  public OgcCoucheRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public OgcCoucheRepository ogcCoucheRepository(DSLContext context) {
    return new OgcCoucheRepository(context);
  }

  OgcCoucheRepository(DSLContext context) {
    this.context = context;
  }

  public List<OgcCouche> getAll(List<ItemFilter> itemFilters) {
    List<OgcCouche> l = null;
    l = context.select().from(OGC_COUCHE).fetchInto(OgcCouche.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(OGC_COUCHE));
  }

  public List<OgcCouche> getByService(Long id) {
    List<OgcCouche> l = null;
    l =
        context
            .select()
            .from(OGC_COUCHE)
            .where(OGC_COUCHE.OGC_SERVICE.eq(id))
            .fetchInto(OgcCouche.class);
    return l;
  }
}
