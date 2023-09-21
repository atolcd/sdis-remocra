package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.OGC_COUCHE;
import static fr.sdis83.remocra.db.model.remocra.Tables.OGC_SERVICE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.model.OgcService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OgcServiceRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  public OgcServiceRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public OgcServiceRepository ogcServiceRepository(DSLContext context) {
    return new OgcServiceRepository(context);
  }

  OgcServiceRepository(DSLContext context) {
    this.context = context;
  }

  public List<OgcService> getAll(List<ItemFilter> itemFilters) {
    List<OgcService> l = null;
    l = context.select().from(OGC_SERVICE).fetchInto(OgcService.class);
    for (OgcService service : l) {
      List<OgcCouche> c =
          context
              .select()
              .from(OGC_COUCHE)
              .where(OGC_COUCHE.OGC_SERVICE.eq(service.getId()))
              .fetchInto(OgcCouche.class);
      service.setOgcCouches(c);
    }
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(OGC_SERVICE));
  }
}
