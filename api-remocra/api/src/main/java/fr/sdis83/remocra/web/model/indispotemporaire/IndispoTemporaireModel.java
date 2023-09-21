package fr.sdis83.remocra.web.model.indispotemporaire;

import java.util.List;

public class IndispoTemporaireModel {

  Long identifiant;

  String date_debut;

  String date_fin;

  String motif;

  String date_rappel_debut;

  String date_rappel_fin;

  String statut;

  Integer total_hydrants;

  Boolean bascule_auto_indispo;

  Boolean bascule_auto_dispo;

  Boolean mel_avant_indispo;

  Boolean mel_avant_dispo;

  List<String> hydrants;

  String organisme_API;

  public Long getIdentifiant() {
    return identifiant;
  }

  public void setIdentifiant(Long identifiant) {
    this.identifiant = identifiant;
  }

  public String getDate_debut() {
    return date_debut;
  }

  public void setDate_debut(String date_debut) {
    this.date_debut = (date_debut != null) ? date_debut.substring(0, 16).replace('T', ' ') : null;
  }

  public String getDate_fin() {
    return date_fin;
  }

  public void setDate_fin(String date_fin) {
    this.date_fin = (date_fin != null) ? date_fin.substring(0, 16).replace('T', ' ') : null;
  }

  public String getMotif() {
    return motif;
  }

  public void setMotif(String motif) {
    this.motif = motif;
  }

  public String getDate_rappel_debut() {
    return date_rappel_debut;
  }

  public void setDate_rappel_debut(String date_rappel_debut) {
    this.date_rappel_debut =
        (date_rappel_debut != null) ? date_rappel_debut.substring(0, 16).replace('T', ' ') : null;
  }

  public String getDate_rappel_fin() {
    return date_rappel_fin;
  }

  public void setDate_rappel_fin(String date_rappel_fin) {
    this.date_rappel_fin =
        (date_rappel_fin != null) ? date_rappel_fin.substring(0, 16).replace('T', ' ') : null;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public Integer getTotal_hydrants() {
    return total_hydrants;
  }

  public void setTotal_hydrants(Integer total_hydrants) {
    this.total_hydrants = total_hydrants;
  }

  public Boolean getBascule_auto_indispo() {
    return bascule_auto_indispo;
  }

  public void setBascule_auto_indispo(Boolean bascule_auto_indispo) {
    this.bascule_auto_indispo = bascule_auto_indispo;
  }

  public Boolean getBascule_auto_dispo() {
    return bascule_auto_dispo;
  }

  public void setBascule_auto_dispo(Boolean bascule_auto_dispo) {
    this.bascule_auto_dispo = bascule_auto_dispo;
  }

  public Boolean getMel_avant_indispo() {
    return mel_avant_indispo;
  }

  public void setMel_avant_indispo(Boolean mel_avant_indispo) {
    this.mel_avant_indispo = mel_avant_indispo;
  }

  public Boolean getMel_avant_dispo() {
    return mel_avant_dispo;
  }

  public void setMel_avant_dispo(Boolean mel_avant_dispo) {
    this.mel_avant_dispo = mel_avant_dispo;
  }

  public List<String> getHydrants() {
    return hydrants;
  }

  public void setHydrants(List<String> hydrants) {
    this.hydrants = hydrants;
  }

  public String getOrganisme_API() {
    return organisme_API;
  }

  public void setOrganisme_API(String organisme_API) {
    this.organisme_API = organisme_API;
  }
}
