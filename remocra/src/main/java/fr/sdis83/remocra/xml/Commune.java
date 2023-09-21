package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlElement;

public class Commune {

  private String insee;

  private String nom;

  private String code;

  public Commune() {
    //
  }

  public Commune(String code, String nom, String insee) {
    this.code = code;
    this.nom = nom;
    this.insee = insee;
  }

  public String getInsee() {
    return insee;
  }

  public void setInsee(String insee) {
    this.insee = insee;
  }

  @XmlElement(name = "libelle")
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

  @Override
  public String toString() {
    return nom + " " + code + " " + insee;
  }
}
