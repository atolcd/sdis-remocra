package fr.sdis83.remocra.web.model.referentiel;

public class ParametreData {

  public String cle;

  public String valeur;

  public ParametreData(String cle, String valeur) {
    this.cle = cle;
    this.valeur = valeur;
  }

  public String getCle() {
    return cle;
  }

  public void setCle(String cle) {
    this.cle = cle;
  }

  public String getValeur() {
    return valeur;
  }

  public void setValeur(String valeur) {
    this.valeur = valeur;
  }
}
