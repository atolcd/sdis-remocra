package fr.sdis83.remocra.web.model.referentiel;

public class CommuneModel {

  Long idRemocra;
  String code;
  String nom;
  String insee;

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

  public String getInsee() {
    return insee;
  }

  public void setInsee(String insee) {
    this.insee = insee;
  }
}
