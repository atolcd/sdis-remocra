package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.tables.Parametre.PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.enums.TypeCategorieParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.referentiel.ParametreData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class ParametreRepository {

  private final DSLContext context;

  @Inject
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

  public List<ParametreData> getParametresMobile() {
    // On ne veut pas certains paramètres qui sont gérés à part
    List<String> listParametresProteges =
        List.of(
            GlobalConstants.PARAMETRE_CARACTERISTIQUE_PENA,
            GlobalConstants.PARAMETRE_CARACTERISTIQUE_PIBI,
            GlobalConstants.PARAMETRE_MDP_ADMINISTRATEUR,
            GlobalConstants.PARAMETRE_MODE_DECONNECTE,
            GlobalConstants.PARAMETRE_DUREE_VALIDITE_TOKEN);

    return context
        .select(PARAMETRE.CLE_PARAMETRE.as("cle"), PARAMETRE.VALEUR_PARAMETRE.as("valeur"))
        .from(PARAMETRE)
        .where(PARAMETRE.CATEGORIE_PARAMETRE.eq(TypeCategorieParametre.MOBILE))
        .and(PARAMETRE.CLE_PARAMETRE.notIn(listParametresProteges))
        .fetchInto(ParametreData.class);
  }
}
