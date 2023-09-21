package fr.sdis83.remocra.xml;

public class Marque {

  private String code;

  private String libelle;

  private LstModeles modeles;

  public Marque() {
    //
  }

  public Marque(String code, String libelle) {
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

  public LstModeles getModeles() {
    return modeles;
  }

  public void setModeles(LstModeles modeles) {
    this.modeles = modeles;
  }

  @Override
  public String toString() {
    return this.code + " " + this.libelle;
  }
}
