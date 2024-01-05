package fr.sdis83.remocra.usecase.importctp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Permet de stocker les infos d'une ligne d'un import CTP */
public class LigneImportCtpData {

  String bilan;
  BilanStyle bilanStyle;

  String insee;
  Integer numeroInterne;

  String dateCtp;

  Integer numeroLigne;

  List<String> warnings = new ArrayList<>();

  LigneImportCtpVisiteData dataVisite;

  public String getBilan() {
    return bilan;
  }

  public void setBilan(String bilan) {
    this.bilan = bilan;
  }

  public BilanStyle getBilanStyle() {
    return bilanStyle;
  }

  public void setBilanStyle(BilanStyle bilanStyle) {
    this.bilanStyle = bilanStyle;
  }

  public String getInsee() {
    return insee;
  }

  public void setInsee(String insee) {
    this.insee = insee;
  }

  public Integer getNumeroInterne() {
    return numeroInterne;
  }

  public void setNumeroInterne(Integer numeroInterne) {
    this.numeroInterne = numeroInterne;
  }

  public LigneImportCtpVisiteData getDataVisite() {
    return dataVisite;
  }

  public void setDataVisite(LigneImportCtpVisiteData dataVisite) {
    this.dataVisite = dataVisite;
  }

  public Integer getNumeroLigne() {
    return numeroLigne;
  }

  public void setNumeroLigne(Integer numeroLigne) {
    this.numeroLigne = numeroLigne;
  }

  public String getDateCtp() {
    return dateCtp;
  }

  public void setDateCtp(String dateCtp) {
    this.dateCtp = dateCtp;
  }

  public void removeWarnings() {
    warnings.clear();
  }

  public void addWarning(String warning) {
    warnings.add(warning);
  }

  public void addAllWarnings(Collection<String> warnings) {
    this.warnings.addAll(warnings);
  }

  public List<String> getWarnings() {
    // Clone pour éviter de manipuler la liste originale, TODO à voir si utile ou non
    return new ArrayList<>(warnings);
  }

  public enum BilanStyle {
    OK("OK"),
    INFO("INFO"),
    WARNING("WARNING"),
    ERREUR("ERREUR");

    private final String code;

    BilanStyle(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }
  }
}
