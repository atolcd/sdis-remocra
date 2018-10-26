package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_NATURE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_STATUT;
import static org.jooq.impl.DSL.select;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseNatureEvenement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseStatut;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeCriseNatureEvenementRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;


  public TypeCriseNatureEvenementRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public TypeCriseNatureEvenementRepository typeCriseStatutRepository(DSLContext context) {
    return new TypeCriseNatureEvenementRepository(context);
  }

  TypeCriseNatureEvenementRepository(DSLContext context) {
    this.context = context;
  }

  public List<TypeCriseNatureEvenement> getAll(List<ItemFilter> itemFilters) {
    List<TypeCriseNatureEvenement> l = null;
    l = context.select().from(TYPE_CRISE_NATURE_EVENEMENT).fetchInto(TypeCriseNatureEvenement.class);
    return l;
  }

  public List<TypeCriseNatureEvenement> getNatureByCrise(Long id) {
    List<TypeCriseNatureEvenement> l = null;
    l = context.select().from(TYPE_CRISE_NATURE_EVENEMENT).where(TYPE_CRISE_NATURE_EVENEMENT.ID
        .in(context.select(CRISE_EVENEMENT.NATURE_EVENEMENT).from(CRISE_EVENEMENT).where(CRISE_EVENEMENT.CRISE.eq(id)).fetchInto(Long.class))).fetchInto(TypeCriseNatureEvenement.class);
    return l;
  }
  public List<TypeCriseNatureEvenement> getNatureById(Long id) {
    List<TypeCriseNatureEvenement> l = null;
    l = context.select().from(TYPE_CRISE_NATURE_EVENEMENT).where(TYPE_CRISE_NATURE_EVENEMENT.ID.eq(id)).fetchInto(TypeCriseNatureEvenement.class);
    return l;
  }


  public int count() {
    return context.fetchCount(context.select().from(TYPE_CRISE_NATURE_EVENEMENT));
  }

}
