package fr.sdis83.remocra.usecase.importctp;

import java.util.Collection;

/** Permet de stocker les infos des visites relatives à une ligne d'import CTP */
public class LigneImportCtpVisiteData {
  Collection<Long> anomalies;
  String date;
  long idHydrant;
  String agent1;
  Integer debit;
  Double pression;
  String observation;

  Double latitude;
  Double longitude;

  public Collection<Long> getAnomalies() {
    return anomalies;
  }

  public void setAnomalies(Collection<Long> anomalies) {
    this.anomalies = anomalies;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public long getIdHydrant() {
    return idHydrant;
  }

  public void setIdHydrant(long idHydrant) {
    this.idHydrant = idHydrant;
  }

  public String getAgent1() {
    return agent1;
  }

  public void setAgent1(String agent1) {
    this.agent1 = agent1;
  }

  public Integer getDebit() {
    return debit;
  }

  public void setDebit(Integer debit) {
    this.debit = debit;
  }

  public Double getPression() {
    return pression;
  }

  public void setPression(Double pression) {
    this.pression = pression;
  }

  public String getObservation() {
    return observation;
  }

  public void setObservation(String observation) {
    this.observation = observation;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public LigneImportCtpVisiteData(
      Collection<Long> anomalies,
      String date,
      long idHydrant,
      String agent1,
      Integer debit,
      Double pression,
      String observation) {
    this.anomalies = anomalies;
    this.date = date;
    this.idHydrant = idHydrant;
    this.agent1 = agent1;
    this.debit = debit;
    this.pression = pression;
    this.observation = observation;
  }

  public LigneImportCtpVisiteData() {
    // rien à faire
  }
}
