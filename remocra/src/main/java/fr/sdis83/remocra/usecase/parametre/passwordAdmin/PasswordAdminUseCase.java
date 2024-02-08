package fr.sdis83.remocra.usecase.parametre.passwordAdmin;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.UpdateParametreUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordAdminUseCase {

  @Autowired ParametreDataProvider parametreDataProvider;
  @Autowired UpdateParametreUseCase updateParametreUseCase;

  public String getPasswordAdmin() {
    return parametreDataProvider.get().getPasswordAdmin();
  }

  public void updatePasswordAdminParam(String passwordAdminParam) {
    updateParametreUseCase.updateParametre(
        GlobalConstants.MDP_ADMINISTRATEUR, String.valueOf(passwordAdminParam));
  }
}
