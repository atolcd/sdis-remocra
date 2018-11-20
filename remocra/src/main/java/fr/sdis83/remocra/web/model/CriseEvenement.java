package fr.sdis83.remocra.web.model;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import org.hibernate.annotations.Formula;
import org.joda.time.Instant;


public class CriseEvenement implements Featurable {


  private Long id;

  private Geometry geometrie;

  private String nom;

  private String description;

  private Instant constat;

  private Instant redefinition;

  private Instant cloture;

  private String origine;

  private String geoJsonGeometry;

  private Long natureId;

  private String natureNom;

  private Integer importance;

  private String tags;

  private TypeCriseNatureEvenement typeCriseNatureEvenement;

  private List<CriseSuivi> criseSuivis;

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

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getConstat() {
    return constat;
  }

  public void setConstat(Instant constat) {
    this.constat = constat;
  }

  public Instant getCloture() {
    return cloture;
  }

  public void setCloture(Instant cloture) {
    this.cloture = cloture;
  }

  public String getOrigine() {
    return origine;
  }

  public void setOrigine(String origine) {
    this.origine = origine;
  }

  public Integer getImportance() {
    return importance;
  }

  public void setImportance(Integer importance) {
    this.importance = importance;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public TypeCriseNatureEvenement getTypeCriseNatureEvenement() {
    return typeCriseNatureEvenement;
  }

  public void setTypeCriseNatureEvenement(TypeCriseNatureEvenement typeCriseNatureEvenement) {
    this.typeCriseNatureEvenement = typeCriseNatureEvenement;
  }

  public Instant getRedefinition() {
    return redefinition;
  }

  public void setRedefinition(Instant redefinition) {
    this.redefinition = redefinition;
  }

  public List<CriseSuivi> getCriseSuivis() {
    return criseSuivis;
  }

  public void setCriseSuivis(List<CriseSuivi> criseSuivis) {
    this.criseSuivis = criseSuivis;
  }


  @Override
  public Feature toFeature() {
    Feature feature = new Feature(this.id, this.getGeoJsonGeometry());
    feature.addProperty("nom", this.getNom());
    feature.addProperty("nature", this.getNatureId());
    feature.addProperty("natureNom", this.getNatureNom());
    feature.addProperty("creation", this.getConstat());
    return feature;
  }

  public String getGeoJsonGeometry() {
    return geoJsonGeometry;
  }

  public void setGeoJsonGeometry(String geoJsonGeometry) {
    this.geoJsonGeometry = geoJsonGeometry;
  }

  public Long getNatureId() {
    return natureId;
  }

  public void setNatureId(Long natureId) {
    this.natureId = natureId;
  }

  public String getNatureNom() {
    return natureNom;
  }

  public void setNatureNom(String natureNom) {
    this.natureNom = natureNom;
  }
}
