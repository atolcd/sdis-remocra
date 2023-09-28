package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.Parametre.PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import java.util.Map;
import java.util.Set;
import org.jooq.DSLContext;

public class ParametreRepository {

  private final DSLContext context;

  public ParametreRepository(DSLContext context) {
    this.context = context;
  }

  public Parametre getParametre(String cle) {
    return context
        .selectFrom(PARAMETRE)
        .where(PARAMETRE.CLE_PARAMETRE.eq(cle))
        .fetchOneInto(Parametre.class);
  }

  public Map<String, Parametre> getParametres(Set<String> cles) {
    return context
        .selectFrom(PARAMETRE)
        .where(PARAMETRE.CLE_PARAMETRE.in(cles))
        .fetchMap(PARAMETRE.CLE_PARAMETRE, Parametre.class);
  }
}
