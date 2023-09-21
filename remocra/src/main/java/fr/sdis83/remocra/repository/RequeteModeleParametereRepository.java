package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModeleParametre.REQUETE_MODELE_PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleParametre;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequeteModeleParametereRepository {

  @Autowired DSLContext context;

  public RequeteModeleParametereRepository() {}

  @Bean
  public RequeteModeleParametereRepository RequeteModeleParametereRepository(DSLContext context) {
    return new RequeteModeleParametereRepository(context);
  }

  RequeteModeleParametereRepository(DSLContext context) {
    this.context = context;
  }

  public List<RequeteModeleParametre> getAll() {
    List<RequeteModeleParametre> l =
        context.select().from(REQUETE_MODELE_PARAMETRE).fetchInto(RequeteModeleParametre.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(REQUETE_MODELE_PARAMETRE));
  }

  public List<RequeteModeleParametre> getByRequeteModele(Long requeteModele) {
    List<RequeteModeleParametre> l =
        context
            .select()
            .from(REQUETE_MODELE_PARAMETRE)
            .where(REQUETE_MODELE_PARAMETRE.REQUETE_MODELE.eq(requeteModele))
            .orderBy(REQUETE_MODELE_PARAMETRE.FORMULAIRE_NUM_ORDRE)
            .fetchInto(RequeteModeleParametre.class);
    return l;
  }
}
