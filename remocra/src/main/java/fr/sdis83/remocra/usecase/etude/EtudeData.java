package fr.sdis83.remocra.usecase.etude;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtude;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtudeStatut;
import java.util.List;

public class EtudeData {
  private Long id;

  private String nom;

  private String numero;

  private String description;

  private String instantMaj;

  public boolean isReseauImporte() {
    return reseauImporte;
  }

  private Long organisme;

  private TypeEtude type;

  private TypeEtudeStatut statut;

  private List<Commune> communes;

  private List<DocumentEtudeData> documents;

  private boolean readOnly;

  private boolean reseauImporte;

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

  public String getInstantMaj() {
    return instantMaj;
  }

  public void setInstantMaj(String instantMaj) {
    this.instantMaj = instantMaj;
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

  public List<Commune> getCommunes() {
    return communes;
  }

  public void setCommunes(List<Commune> communes) {
    this.communes = communes;
  }

  public List<DocumentEtudeData> getDocuments() {
    return documents;
  }

  public void setDocuments(List<DocumentEtudeData> documents) {
    this.documents = documents;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public void setReseauImporte(boolean reseauImporte) {
    this.reseauImporte = reseauImporte;
  }

  public Long getOrganisme() {
    return organisme;
  }

  public void setOrganisme(Long organisme) {
    this.organisme = organisme;
  }
}
