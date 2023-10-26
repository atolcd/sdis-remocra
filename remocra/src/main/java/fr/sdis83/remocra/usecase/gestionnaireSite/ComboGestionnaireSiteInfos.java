package fr.sdis83.remocra.usecase.gestionnaireSite;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ComboGestionnaireSiteInfos {
  private Long idGestionnaireSite;
  private String nomGestionnaireSite;

  public ComboGestionnaireSiteInfos() {}

  public ComboGestionnaireSiteInfos(Long idGestionnaireSite, String nomGestionnaireSite) {
    this.idGestionnaireSite = idGestionnaireSite;
    this.nomGestionnaireSite = nomGestionnaireSite;
  }

  public Long getIdGestionnaireSite() {
    return idGestionnaireSite;
  }

  public void setIdGestionnaireSite(Long idGestionnaireSite) {
    this.idGestionnaireSite = idGestionnaireSite;
  }

  public String getNomGestionnaireSite() {
    return nomGestionnaireSite;
  }

  public void setNomGestionnaireSite(String nomGestionnaireSite) {
    this.nomGestionnaireSite = nomGestionnaireSite;
  }
}
