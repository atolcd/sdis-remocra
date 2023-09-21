package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCrise;
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
public class TypeCriseRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  public TypeCriseRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public TypeCriseRepository typeCriseRepository(DSLContext context) {
    return new TypeCriseRepository(context);
  }

  TypeCriseRepository(DSLContext context) {
    this.context = context;
  }

  public List<TypeCrise> getAll(List<ItemFilter> itemFilters) {

    // On récupere les requêtes en fonction des profils
    List<TypeCrise> l = null;
    l = context.select().from(TYPE_CRISE).fetchInto(TypeCrise.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(TYPE_CRISE));
  }
}
