package fr.sdis83.remocra.web.model.referentiel;

public class TypeHydrantSaisieModel {

  Long idRemocra;
  String code;
  String nom;
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

  public Boolean getActif() {
    return actif;
  }

  public void setActif(Boolean actif) {
    this.actif = actif;
  }
}
