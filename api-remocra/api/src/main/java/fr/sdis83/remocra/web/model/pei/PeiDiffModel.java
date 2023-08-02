package fr.sdis83.remocra.web.model.pei;

import java.util.Date;

public class PeiDiffModel {
  Long idHydrant;
  String numero;

  Date dateModification;

  String utilisateurModification;

  String utilisateurModificationOrganisme;

  String organismeModification;

  String auteurModificationFlag;

  String operation;

  String type;

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public Date getDateModification() {
    return dateModification;
  }

  public void setDateModification(Date dateModification) {
    this.dateModification = dateModification;
  }

  public String getUtilisateurModification() {
    return utilisateurModification;
  }

  public void setUtilisateurModification(String utilisateurModification) {
    this.utilisateurModification = utilisateurModification;
  }

  public String getUtilisateurModificationOrganisme() {
    return utilisateurModificationOrganisme;
  }

  public void setUtilisateurModificationOrganisme(String utilisateurModificationOrganisme) {
    this.utilisateurModificationOrganisme = utilisateurModificationOrganisme;
  }

  public String getOrganismeModification() {
    return organismeModification;
  }

  public void setOrganismeModification(String organismeModification) {
    this.organismeModification = organismeModification;
  }

  public String getAuteurModificationFlag() {
    return auteurModificationFlag;
  }

  public void setAuteurModificationFlag(String auteurModificationFlag) {
    this.auteurModificationFlag = auteurModificationFlag;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getIdHydrant() {
    return idHydrant;
  }

  public void setIdHydrant(Long idHydrant) {
    this.idHydrant = idHydrant;
  }
}
