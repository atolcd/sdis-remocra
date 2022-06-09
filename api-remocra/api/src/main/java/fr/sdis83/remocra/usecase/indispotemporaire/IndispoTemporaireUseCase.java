package fr.sdis83.remocra.usecase.indispotemporaire;

import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.repository.IndispoTemporaireRepository;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireModel;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE_HYDRANT;

public class IndispoTemporaireUseCase {

  @Inject
  IndispoTemporaireRepository indispoTemporaireRepository;

  private final DSLContext context;

  @Inject @CurrentUser
  Provider<UserInfo> currentUser;

  @Inject
  PeiUseCase peiUseCase;

  @Inject
  public IndispoTemporaireUseCase(DSLContext context) {
    this.context = context;
  }

  public List<IndispoTemporaireModel> getAll(String organismeApi, Integer limit, Integer start) {
    return this.indispoTemporaireRepository.getAll(organismeApi, limit, start);
  }

  /**
   * Vérifie si une indisponibilité temporaire est accessible à l'utilisateur
   * Si au moins un PEI composant l'IT est accessible, l'indispo temporaire est entièrement accessible
   * @param id L'identifiant de l'indisponibilité temporaire
   */
  private boolean isIndispoTemporaireAccessible(Long id) {
    List<String> hydrants = context
      .select(HYDRANT.NUMERO)
      .from(HYDRANT)
      .join(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT.eq(HYDRANT.ID))
      .join(HYDRANT_INDISPO_TEMPORAIRE).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE.eq(HYDRANT_INDISPO_TEMPORAIRE.ID))
      .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(id))
      .fetchInto(String.class);

    boolean isAccessible = false;
    for(String s : hydrants) {
      if(this.peiUseCase.isPeiAccessible(s)) {
        isAccessible = true;
      }
    }
    return isAccessible;
  }
}
