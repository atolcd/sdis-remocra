package fr.sdis83.remocra.xml;

public class Positionnement {

  private String code;

  private String libelle;

  public Positionnement() {
    //
  }

  public Positionnement(String code, String libelle) {
    this.code = code;
    this.libelle = libelle;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLibelle() {
    return libelle;
  }

  public void setLibelle(String libelle) {
    this.libelle = libelle;
  }

  @Override
  public String toString() {
    return this.code + " " + this.libelle;
  }
}
