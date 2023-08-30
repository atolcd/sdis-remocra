package fr.sdis83.remocra.web.model.referentiel;

public class TypeHydrantAnomalieNatureModel {

  Long idRemocra;
  Long idTypeHydrantAnomalie;
  Long idTypeHydrantNature;
  Integer valIndispoTerrestre;
  Integer valIndispoHbe;
  Integer valIndispoAdmin;

  public Long getIdRemocra() {
    return idRemocra;
  }

  public void setIdRemocra(Long idRemocra) {
    this.idRemocra = idRemocra;
  }

  public Long getIdTypeHydrantAnomalie() {
    return idTypeHydrantAnomalie;
  }

  public void setIdTypeHydrantAnomalie(Long idTypeHydrantAnomalie) {
    this.idTypeHydrantAnomalie = idTypeHydrantAnomalie;
  }

  public Long getIdTypeHydrantNature() {
    return idTypeHydrantNature;
  }

  public void setIdTypeHydrantNature(Long idTypeHydrantNature) {
    this.idTypeHydrantNature = idTypeHydrantNature;
  }

  public Integer getValIndispoTerrestre() {
    return valIndispoTerrestre;
  }

  public void setValIndispoTerrestre(Integer valIndispoTerrestre) {
    this.valIndispoTerrestre = valIndispoTerrestre;
  }

  public Integer getValIndispoHbe() {
    return valIndispoHbe;
  }

  public void setValIndispoHbe(Integer valIndispoHbe) {
    this.valIndispoHbe = valIndispoHbe;
  }

  public Integer getValIndispoAdmin() {
    return valIndispoAdmin;
  }

  public void setValIndispoAdmin(Integer valIndispoAdmin) {
    this.valIndispoAdmin = valIndispoAdmin;
  }
}
