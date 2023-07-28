package fr.sdis83.remocra.web.model.referentiel;

public class GestionnaireModel {

  Long idRemocra;
  String nom;
  String code;
  Boolean actif;

  public Long getIdRemocra() {
    return idRemocra;
  }

  public void setIdRemocra(Long idRemocra) {
    this.idRemocra = idRemocra;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Boolean getActif() {
    return actif;
  }

  public void setActif(Boolean actif) {
    this.actif = actif;
  }
}
