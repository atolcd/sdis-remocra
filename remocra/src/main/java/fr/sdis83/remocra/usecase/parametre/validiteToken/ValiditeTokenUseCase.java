package fr.sdis83.remocra.usecase.parametre.validiteToken;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.UpdateParametreUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValiditeTokenUseCase {

  @Autowired ParametreDataProvider parametreDataProvider;
  @Autowired UpdateParametreUseCase updateParametreUseCase;

  public int getValiditeToken() {
    return parametreDataProvider.get().getValiditeToken();
  }

  public void updateValiditeTokenParam(int validiteToken) {
    updateParametreUseCase.updateParametre(
        GlobalConstants.DUREE_VALIDITE_TOKEN, String.valueOf(validiteToken));
  }
}
