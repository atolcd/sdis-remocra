package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"code", "libelle", "critere", "anomaliesNatures"})
public class Anomalie {

  private String code;

  private String libelle;

  private String critere;

  private LstAnomaliesNatures anomaliesNatures;

  public Anomalie() {
    //
  }

  public Anomalie(String code, String libelle, String critere) {
    this.code = code;
    this.libelle = libelle;
    this.critere = critere;
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

  public String getCritere() {
    return critere;
  }

  public void setCritere(String critere) {
    this.critere = critere;
  }

  @XmlElement(name = "natures")
  public LstAnomaliesNatures getAnomaliesNatures() {
    return anomaliesNatures;
  }

  public void setAnomaliesNatures(LstAnomaliesNatures anomaliesNatures) {
    this.anomaliesNatures = anomaliesNatures;
  }

  @Override
  public String toString() {
    return this.code + " " + this.libelle + " " + this.critere;
  }
}
