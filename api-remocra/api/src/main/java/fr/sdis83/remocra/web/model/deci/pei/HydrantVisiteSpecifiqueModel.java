package fr.sdis83.remocra.web.model.deci.pei;

import java.util.List;

public class HydrantVisiteSpecifiqueModel {

  public String date;

  public Long identifiant;

  public String contexte;

  public String agent1;

  public String agent2;

  public int debit;

  public int debitMax;

  public double pression;

  public double pressionDyn;

  public double pressionDynDeb;

  public boolean ctrlDebitPression;

  public List<String> anomaliesConstatees;

  public List<String> anomaliesControlees;

  public String observations;

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

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
    this.date = date.substring(0,16).replace('T', ' ');
  }

  public String getAgent1() {
    return agent1;
  }

  public void setAgent1(String agent1) {
    this.agent1 = agent1;
  }

  public String getAgent2() {
    return agent2;
  }

  public void setAgent2(String agent2) {
    this.agent2 = agent2;
  }

  public boolean isCtrlDebitPression() {
    return ctrlDebitPression;
  }

  public void setCtrlDebitPression(boolean ctrlDebitPression) {
    this.ctrlDebitPression = ctrlDebitPression;
  }

  public int getDebit() {
    return debit;
  }

  public void setDebit(int debit) {
    this.debit = debit;
  }

  public int getDebitMax() {
    return debitMax;
  }

  public void setDebitMax(int debitMax) {
    this.debitMax = debitMax;
  }

  public double getPression() {
    return pression;
  }

  public void setPression(double pression) {
    this.pression = pression;
  }

  public double getPressionDyn() {
    return pressionDyn;
  }

  public void setPressionDyn(double pressionDyn) {
    this.pressionDyn = pressionDyn;
  }

  public double getPressionDynDeb() {
    return pressionDynDeb;
  }

  public void setPressionDynDeb(double pressionDynDeb) {
    this.pressionDynDeb = pressionDynDeb;
  }

  public List<String> getAnomaliesConstatees() {
    return anomaliesConstatees;
  }

  public void setAnomaliesConstatees(List<String> anomaliesConstatees) {
    this.anomaliesConstatees = anomaliesConstatees;
  }

  public List<String> getAnomaliesControlees() {
    return anomaliesControlees;
  }

  public void setAnomaliesControlees(List<String> anomaliesControlees) {
    this.anomaliesControlees = anomaliesControlees;
  }

  public Long getIdentifiant() {
    return identifiant;
  }

  public void setIdentifiant(Long identifiant) {
    this.identifiant = identifiant;
  }
}
