package fr.sdis83.remocra.web.model;

import java.util.List;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.domain.remocra.Commune;
import org.joda.time.Instant;


public class Crise {


  private Long id;

  private String nom;

  private String description;

  private Instant cloture;

  private Instant activation;

  private TypeCrise typeCrise;

  private TypeCriseStatut typeCriseStatut;

  private String carte;

  private List<Commune> communes;

  public List<Commune> getCommunes() {
    return communes;
  }

  public void setCommunes(List<Commune> communes) {
    this.communes = communes;
  }

  private List<RepertoireLieu> repertoireLieus;

  public List<RepertoireLieu> getRepertoireLieus() {
    return repertoireLieus;
  }

  public void setRepertoireLieus(List<RepertoireLieu> repertoireLieus) {
    this.repertoireLieus = repertoireLieus;
  }

  private List<ProcessusEtlPlanification> processusEtlPlanifications;

  private List<OgcCouche> ogcCouches;



  public TypeCrise getTypeCrise() {
    return typeCrise;
  }

  public void setTypeCrise(TypeCrise typeCrise) {
    this.typeCrise = typeCrise;
  }

  public TypeCriseStatut getTypeCriseStatut() {
    return typeCriseStatut;
  }

  public void setTypeCriseStatut(TypeCriseStatut typeCriseStatut) {
    this.typeCriseStatut = typeCriseStatut;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getCloture() {
    return cloture;
  }

  public void setCloture(Instant cloture) {
    this.cloture = cloture;
  }

  public Instant getActivation() {
    return activation;
  }

  public void setActivation(Instant activation) {
    this.activation = activation;
  }

  public Long getId() {

    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<ProcessusEtlPlanification> getProcessusEtlPlanifications() {
    return processusEtlPlanifications;
  }

  public void setProcessusEtlPlanifications(List<ProcessusEtlPlanification> processusEtlPlanifications) {
    this.processusEtlPlanifications = processusEtlPlanifications;
  }

  public String getCarte() {
    return carte;
  }

  public void setCarte(String carte) {
    this.carte = carte;
  }

  public List<OgcCouche> getOgcCouches() {
    return ogcCouches;
  }

  public void setOgcCouches(List<OgcCouche> ogcCouches) {
    this.ogcCouches = ogcCouches;
  }

}
