package fr.sdis83.remocra.usecase.parametre.affichageIndispo;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.UpdateParametreUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AffichageIndispoUseCase {

  @Autowired ParametreDataProvider parametreDataProvider;
  @Autowired UpdateParametreUseCase updateParametreUseCase;

  public Boolean getAffichageIndispo() {
    return parametreDataProvider.get().getAffichageIndispo();
  }

  public void updateAffichageIndispoParam(Boolean affichageIndispoParam) {
    updateParametreUseCase.updateParametre(
        GlobalConstants.AFFICHAGE_INDISPO, String.valueOf(affichageIndispoParam));
  }
}
