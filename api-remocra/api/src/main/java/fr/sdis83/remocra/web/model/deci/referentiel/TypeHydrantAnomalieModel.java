package fr.sdis83.remocra.web.model.deci.referentiel;

public class TypeHydrantAnomalieModel {

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

  public String getContexte() {
    return contexte;
  }

  public void setContexte(String contexte) {
    this.contexte = contexte;
  }

  String code;

  String nom;

  String contexte;
}
