package fr.sdis83.remocra.usecase.parametre.bridagephoto;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.UpdateParametreUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BridagePhotoUseCase {

  @Autowired ParametreDataProvider parametreDataProvider;
  @Autowired UpdateParametreUseCase updateParametreUseCase;

  public Boolean getBridagePhoto() {
    return parametreDataProvider.get().getBridagePhoto();
  }

  public void updateBridagePhoto(Boolean bridagePhoto) {
    updateParametreUseCase.updateParametre(
        GlobalConstants.BRIDAGE_PHOTO, String.valueOf(bridagePhoto));
  }
}
