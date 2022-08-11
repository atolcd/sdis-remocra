package fr.sdis83.remocra.web.model;

import com.vividsolutions.jts.geom.Geometry;

public class CommuneRecord {

  private Long id;

  private Geometry geometrie;

  private String insee;

  private String nom;

  private boolean pprif;

  private String code;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Geometry getGeometrie() {
    return geometrie;
  }

  public void setGeometrie(Geometry geometrie) {
    this.geometrie = geometrie;
  }

  public String getInsee() {
    return insee;
  }

  public void setInsee(String insee) {
    this.insee = insee;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public boolean isPprif() {
    return pprif;
  }

  public void setPprif(boolean pprif) {
    this.pprif = pprif;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
