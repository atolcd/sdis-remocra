package fr.sdis83.remocra.domain.datasource;

import java.io.Serializable;

public class CodeLibelleOrdreData implements Serializable {
  private String code;
  private String libelle;
  private Integer ordre;

  // sert lors de l'appelle de new TypeReference<ArrayList<CodeLibelleOrdreData>>()
  // sur la récupération du front
  public CodeLibelleOrdreData() {}

  public CodeLibelleOrdreData(String code, String libelle) {

    this(code, libelle, null);
  }

  public CodeLibelleOrdreData(String code, String libelle, Integer ordre) {
    this.code = code;
    this.libelle = libelle;
    this.ordre = ordre;
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

  public Integer getOrdre() {
    return ordre;
  }

  public void setOrdre(Integer ordre) {
    this.ordre = ordre;
  }
}
