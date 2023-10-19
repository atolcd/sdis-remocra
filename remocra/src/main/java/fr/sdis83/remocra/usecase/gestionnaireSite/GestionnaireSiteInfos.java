package fr.sdis83.remocra.usecase.gestionnaireSite;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.GestionnaireSite;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GestionnaireSiteInfos {
  private GestionnaireSite gestionnaireSite;
  private String gestionnaireName;

  public GestionnaireSiteInfos() {}

  public GestionnaireSiteInfos(GestionnaireSite gestionnaireSite, String gestionnaireName) {
    this.gestionnaireSite = gestionnaireSite;
    this.gestionnaireName = gestionnaireName;
  }

  public GestionnaireSiteInfos(GestionnaireSiteInfos gestionnaireSiteInfos) {
    new GestionnaireSiteInfos(
        gestionnaireSiteInfos.gestionnaireSite, gestionnaireSiteInfos.gestionnaireName);
  }

  public GestionnaireSite getGestionnaireSite() {
    return gestionnaireSite;
  }

  public String getGestionnaireName() {
    return gestionnaireName;
  }
}
