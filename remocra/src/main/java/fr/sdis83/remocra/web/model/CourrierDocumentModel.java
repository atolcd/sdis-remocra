package fr.sdis83.remocra.web.model;

import java.util.Date;

public class CourrierDocumentModel {

  private Long id;

  private Long document;

  private String code;

  private String nomDestinataire;

  private String typeDestinataire;

  private Long idDestinataire;

  private Date accuse;

  private String mail;

  private Date dateDoc;

  private String nomDocument;

  private String codeDocument;

  private String reference;

  private String expediteur;

  private String objet;

  private Boolean isUtilisateurDestinataire;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDocument() {
    return document;
  }

  public void setDocument(Long document) {
    this.document = document;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getNomDestinataire() {
    return nomDestinataire;
  }

  public void setNomDestinataire(String nomDestinataire) {
    this.nomDestinataire = nomDestinataire;
  }

  public String getTypeDestinataire() {
    return typeDestinataire;
  }

  public void setTypeDestinataire(String typeDestinataire) {
    this.typeDestinataire = typeDestinataire;
  }

  public Long getIdDestinataire() {
    return idDestinataire;
  }

  public void setIdDestinataire(Long idDestinataire) {
    this.idDestinataire = idDestinataire;
  }

  public Date getAccuse() {
    return accuse;
  }

  public void setAccuse(Date accuse) {
    this.accuse = accuse;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public Date getDateDoc() {
    return dateDoc;
  }

  public void setDateDoc(Date dateDoc) {
    this.dateDoc = dateDoc;
  }

  public String getNomDocument() {
    return nomDocument;
  }

  public void setNomDocument(String nomDocument) {
    this.nomDocument = nomDocument;
  }

  public String getCodeDocument() {
    return codeDocument;
  }

  public void setCodeDocument(String codeDocument) {
    this.codeDocument = codeDocument;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getExpediteur() {
    return expediteur;
  }

  public void setExpediteur(String expediteur) {
    this.expediteur = expediteur;
  }

  public String getObjet() {
    return objet;
  }

  public void setObjet(String objet) {
    this.objet = objet;
  }

  public Boolean getUtilisateurDestinataire() {
    return isUtilisateurDestinataire;
  }

  public void setUtilisateurDestinataire(Boolean utilisateurDestinataire) {
    isUtilisateurDestinataire = utilisateurDestinataire;
  }
}
