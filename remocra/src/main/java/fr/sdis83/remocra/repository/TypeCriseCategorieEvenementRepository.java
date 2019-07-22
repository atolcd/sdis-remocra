package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_CATEGORIE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_EVENEMENT_CRISE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_EVENEMENT_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_NATURE_EVENEMENT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCrise;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseCategorieEvenement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseNatureEvenement;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeCriseCategorieEvenementRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;

  @Autowired
  UtilisateurService utilisateurService;

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

  public List<TypeCriseCategorieEvenement> getAll(List<ItemFilter> itemFilters, Long id) throws BusinessException {

    //On filtre par type_crise et par profil_droit
    Long tc = context.select(CRISE.TYPE_CRISE).from(CRISE).where(CRISE.ID.eq(id)).fetchOne().value1();
    List<TypeCriseCategorieEvenement> l = null;
    l = context.select().from(TYPE_CRISE_CATEGORIE_EVENEMENT)
        .join(TYPE_CRISE_EVENEMENT_CRISE).on(TYPE_CRISE_EVENEMENT_CRISE.CATEGORIE_EVENEMENT.eq(TYPE_CRISE_CATEGORIE_EVENEMENT.ID))
        .join(TYPE_CRISE_EVENEMENT_DROIT).on(TYPE_CRISE_EVENEMENT_DROIT.CATEGORIE_EVENEMENT.eq(TYPE_CRISE_CATEGORIE_EVENEMENT.ID))
        .where(TYPE_CRISE_EVENEMENT_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentProfilDroit().getId()))
        .and(TYPE_CRISE_EVENEMENT_CRISE.TYPE_CRISE.eq(tc)).fetchInto(TypeCriseCategorieEvenement.class);
    return l;
  }
}
