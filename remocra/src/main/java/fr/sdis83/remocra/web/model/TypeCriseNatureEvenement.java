package fr.sdis83.remocra.web.model;

public class TypeCriseNatureEvenement {

  private Long id;

  private String nom;

  private String code;


  private Boolean actif;

  private String typeGeometrie;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Boolean getActif() {
    return actif;
  }

  public void setActif(Boolean actif) {
    this.actif = actif;
  }

  public String getTypeGeometrie() {
    return typeGeometrie;
  }

  public void setTypeGeometrie(String typeGeometrie) {
    this.typeGeometrie = typeGeometrie;
  }


}