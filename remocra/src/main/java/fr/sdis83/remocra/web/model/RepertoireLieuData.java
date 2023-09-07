package fr.sdis83.remocra.web.model;

import com.vividsolutions.jts.geom.Geometry;

public class RepertoireLieuData {

  private String libelle;

  private Geometry geometrie;

  private String origine;


  public String getLibelle() {
    return libelle;
  }

  public void setLibelle(String libelle) {
    this.libelle = libelle;
  }

  public Geometry getGeometrie() {
    return geometrie;
  }

  public void setGeometrie(Geometry geometrie) {
    this.geometrie = geometrie;
  }

  public String getOrigine() {
    return origine;
  }

  public void setOrigine(String origine) {
    this.origine = origine;
  }
}
