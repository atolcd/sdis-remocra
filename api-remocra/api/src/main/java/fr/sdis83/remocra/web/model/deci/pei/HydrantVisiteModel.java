package fr.sdis83.remocra.web.model.deci.pei;

import java.util.List;

public class HydrantVisiteModel {

  public String date;

  public String contexte;

  public String identifiant;

  public List<String> anomalies;

  public String getContexte() {
    return contexte;
  }

  public void setContexte(String contexte) {
    this.contexte = contexte;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date.substring(0, 16).replace('T', ' ');
  }

  public String getIdentifiant() {
    return identifiant;
  }

  public void setIdentifiant(String identifiant) {
    this.identifiant = identifiant;
  }

  public List<String> getAnomalies() {
    return anomalies;
  }

  public void setAnomalies(List<String> anomalies) {
    this.anomalies = anomalies;
  }
}
