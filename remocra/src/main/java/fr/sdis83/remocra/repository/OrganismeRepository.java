package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Organisme;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganismeRepository {

  @Autowired DSLContext context;

  public OrganismeRepository() {}

  @Bean
  public OrganismeRepository organismeRepository(DSLContext context) {
    return new OrganismeRepository(context);
  }

  OrganismeRepository(DSLContext context) {
    this.context = context;
  }

  public Organisme getOrganismeWithIdUser(long idUtilisateur) {
    return context
        .select(ORGANISME.fields())
        .from(ORGANISME)
        .join(UTILISATEUR)
        .on(UTILISATEUR.ORGANISME.eq(ORGANISME.ID))
        .where(UTILISATEUR.ID.eq(idUtilisateur))
        .fetchOneInto(Organisme.class);
  }
}
