package fr.sdis83.remocra.web.model;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Document;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import org.joda.time.Instant;

public class CriseDocument {


  private Long   id;
  private String sousType;
  private Long   crise;
  private Long   evenement;
  private String geometrie;
  private String  code;
  private Instant date;
  private String  fichier;
  private String  repertoire;
  private String  type;
  private Instant dateDoc;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSousType() {
    return sousType;
  }

  public void setSousType(String sousType) {
    this.sousType = sousType;
  }



  public Long getCrise() {
    return crise;
  }

  public void setCrise(Long crise) {
    this.crise = crise;
  }

  public Long getEvenement() {
    return evenement;
  }

  public void setEvenement(Long evenement) {
    this.evenement = evenement;
  }

  public String getGeometrie() {
    return geometrie;
  }

  public void setGeometrie(String geometrie) {
    this.geometrie = geometrie;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }

  public String getFichier() {
    return fichier;
  }

  public void setFichier(String fichier) {
    this.fichier = fichier;
  }

  public String getRepertoire() {
    return repertoire;
  }

  public void setRepertoire(String repertoire) {
    this.repertoire = repertoire;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Instant getDateDoc() {
    return dateDoc;
  }

  public void setDateDoc(Instant dateDoc) {
    this.dateDoc = dateDoc;
  }
}
