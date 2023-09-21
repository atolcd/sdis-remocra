package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_STATUT;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseStatut;
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
public class TypeCriseStatutRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  public TypeCriseStatutRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public TypeCriseStatutRepository typeCriseStatutRepository(DSLContext context) {
    return new TypeCriseStatutRepository(context);
  }

  TypeCriseStatutRepository(DSLContext context) {
    this.context = context;
  }

  public List<TypeCriseStatut> getAll(List<ItemFilter> itemFilters) {

    // On récupere les requêtes en fonction des profils
    List<TypeCriseStatut> l = null;
    l = context.select().from(TYPE_CRISE_STATUT).fetchInto(TypeCriseStatut.class);
    return l;
  }

  public TypeCriseStatut getByCode(String code) {

    // On récupere les requêtes en fonction des profils
    TypeCriseStatut tcs =
        context
            .select()
            .from(TYPE_CRISE_STATUT)
            .where(TYPE_CRISE_STATUT.CODE.eq(code))
            .fetchInto(TypeCriseStatut.class)
            .get(0);
    return tcs;
  }

  public int count() {
    return context.fetchCount(context.select().from(TYPE_CRISE_STATUT));
  }
}
