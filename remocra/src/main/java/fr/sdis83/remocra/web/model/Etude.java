package fr.sdis83.remocra.web.model;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtude;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtudeStatut;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Document;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Organisme;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Map;

public class Etude {
  private Long id;

  private String nom;

  private String numero;

  private String description;

  private Instant date_maj;

  private TypeEtude type;

  private TypeEtudeStatut statut;

  private Organisme organisme;

  private ArrayList<Commune> communes;

  private ArrayList<Document> documents;

  private Map<String, String> documentsNoms;

  private boolean readOnly;

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

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getDate_maj() {
    return date_maj;
  }

  public void setDate_maj(Instant date_maj) {
    this.date_maj = date_maj;
  }

  public TypeEtude getType() {
    return type;
  }

  public void setType(TypeEtude type) {
    this.type = type;
  }

  public TypeEtudeStatut getStatut() {
    return statut;
  }

  public void setStatut(TypeEtudeStatut statut) {
    this.statut = statut;
  }

  public ArrayList<Commune> getCommunes() {
    return communes;
  }

  public void setCommunes(ArrayList<Commune> communes) {
    this.communes = communes;
  }

  public ArrayList<Document> getDocuments() {
    return documents;
  }

  public void setDocuments(ArrayList<Document> documents) {
    this.documents = documents;
  }

  public Map<String, String> getDocumentsNoms() {
    return documentsNoms;
  }

  public void setDocumentsNoms(Map<String, String> documentsNoms) {
    this.documentsNoms = documentsNoms;
  }

  public Organisme getOrganisme() {
    return organisme;
  }

  public void setOrganisme(Organisme organisme) {
    this.organisme = organisme;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
}