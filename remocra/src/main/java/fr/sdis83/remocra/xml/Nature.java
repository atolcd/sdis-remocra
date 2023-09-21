package fr.sdis83.remocra.xml;

public class Nature {

  private String code;

  private String libelle;

  private String type;

  public Nature() {
    //
  }

  public Nature(String code, String libelle, String type) {
    this.code = code;
    this.libelle = libelle;
    this.type = type;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return this.code + " " + this.libelle + " " + this.type;
  }
}
