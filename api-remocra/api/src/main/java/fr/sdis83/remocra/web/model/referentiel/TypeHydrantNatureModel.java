package fr.sdis83.remocra.web.model.referentiel;

public class TypeHydrantNatureModel {

  Long idRemocra;
  String code;
  String nom;
  Long idTypeHydrant;
  Boolean actif;

  public Long getIdRemocra() {
    return idRemocra;
  }

  public void setIdRemocra(Long idRemocra) {
    this.idRemocra = idRemocra;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public Long getIdTypeHydrant() {
    return idTypeHydrant;
  }

  public void setIdTypeHydrant(Long idTypeHydrant) {
    this.idTypeHydrant = idTypeHydrant;
  }

  public Boolean getActif() {
    return actif;
  }

  public void setActif(Boolean actif) {
    this.actif = actif;
  }
}
