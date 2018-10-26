package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_CATEGORIE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_NATURE_EVENEMENT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseCategorieEvenement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseNatureEvenement;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeCriseCategorieEvenementRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;


  public TypeCriseCategorieEvenementRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public TypeCriseCategorieEvenementRepository typeCriseCategorieEvenementRepository(DSLContext context) {
    return new TypeCriseCategorieEvenementRepository(context);
  }

  TypeCriseCategorieEvenementRepository(DSLContext context) {
    this.context = context;
  }

  public List<TypeCriseCategorieEvenement> getAll(List<ItemFilter> itemFilters) {

    //On récupere les requêtes en fonction des profils
    List<TypeCriseCategorieEvenement> l = null;
    l = context.select().from(TYPE_CRISE_CATEGORIE_EVENEMENT).fetchInto(TypeCriseCategorieEvenement.class);
    return l;
  }



  public int count() {
    return context.fetchCount(context.select().from(TYPE_CRISE_CATEGORIE_EVENEMENT));
  }

}
