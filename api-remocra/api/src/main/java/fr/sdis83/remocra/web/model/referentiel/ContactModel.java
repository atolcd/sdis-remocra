package fr.sdis83.remocra.web.model.referentiel;

public class ContactModel {

  Long idRemocra;
  Long idRemocraGestionnaire;
  String fonction;
  String civilite;
  String nom;
  String prenom;
  String numeroVoie;
  String voie;
  String suffixeVoie;
  String lieuDit;
  String codePostal;
  String ville;
  String pays;
  String telephone;
  String email;

  public ContactModel(
      Long idRemocra,
      Long idRemocraGestionnaire,
      String fonction,
      String civilite,
      String nom,
      String prenom,
      String numeroVoie,
      String voie,
      String suffixeVoie,
      String lieuDit,
      String codePostal,
      String ville,
      String pays,
      String telephone,
      String email) {
    this.idRemocra = idRemocra;
    this.idRemocraGestionnaire = idRemocraGestionnaire;
    this.fonction = fonction;
    this.civilite = civilite;
    this.nom = nom;
    this.prenom = prenom;
    this.numeroVoie = numeroVoie;
    this.voie = voie;
    this.suffixeVoie = suffixeVoie;
    this.lieuDit = lieuDit;
    this.codePostal = codePostal;
    this.ville = ville;
    this.pays = pays;
    this.telephone = telephone;
    this.email = email;
  }

  public Long getIdRemocra() {
    return idRemocra;
  }

  public void setIdRemocra(Long idRemocra) {
    this.idRemocra = idRemocra;
  }

  public Long getIdRemocraGestionnaire() {
    return idRemocraGestionnaire;
  }

  public void setIdRemocraGestionnaire(Long idRemocraGestionnaire) {
    this.idRemocraGestionnaire = idRemocraGestionnaire;
  }

  public String getFonction() {
    return fonction;
  }

  public void setFonction(String fonction) {
    this.fonction = fonction;
  }

  public String getCivilite() {
    return civilite;
  }

  public void setCivilite(String civilite) {
    this.civilite = civilite;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getNumeroVoie() {
    return numeroVoie;
  }

  public void setNumeroVoie(String numeroVoie) {
    this.numeroVoie = numeroVoie;
  }

  public String getVoie() {
    return voie;
  }

  public void setVoie(String voie) {
    this.voie = voie;
  }

  public String getSuffixeVoie() {
    return suffixeVoie;
  }

  public void setSuffixeVoie(String suffixeVoie) {
    this.suffixeVoie = suffixeVoie;
  }

  public String getLieuDit() {
    return lieuDit;
  }

  public void setLieuDit(String lieuDit) {
    this.lieuDit = lieuDit;
  }

  public String getCodePostal() {
    return codePostal;
  }

  public void setCodePostal(String codePostal) {
    this.codePostal = codePostal;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  public String getPays() {
    return pays;
  }

  public void setPays(String pays) {
    this.pays = pays;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
