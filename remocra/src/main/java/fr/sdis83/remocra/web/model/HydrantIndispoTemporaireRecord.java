package fr.sdis83.remocra.web.model;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.domain.remocra.Hydrant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HydrantIndispoTemporaireRecord {

  private Long id;

  private Date dateDebut;

  private Date dateFin;

  private String motif;

  private Date dateRappelDebut;

  private Date dateRappelFin;

  private TypeHydrantIndispoStatutRecord statut;

  private Integer totalHydrants;

  private boolean basculeAutoIndispo;

  private boolean basculeAutoDispo;

  private boolean mailAvantIndispo;

  private boolean mailAvantDispo;

  private String observation;

  private String commune;

  private List<HydrantRecord> hydrants;

  private Geometry geometrie;

  private String hydrantsTooltip;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(Date dateDebut) {
    this.dateDebut = dateDebut;
  }

  public Date getDateFin() {
    return dateFin;
  }

  public void setDateFin(Date dateFin) {
    this.dateFin = dateFin;
  }

  public String getMotif() {
    return motif;
  }

  public void setMotif(String motif) {
    this.motif = motif;
  }

  public Date getDateRappelDebut() {
    return dateRappelDebut;
  }

  public void setDateRappelDebut(Date dateRappelDebut) {
    this.dateRappelDebut = dateRappelDebut;
  }

  public Date getDateRappelFin() {
    return dateRappelFin;
  }

  public void setDateRappelFin(Date dateRappelFin) {
    this.dateRappelFin = dateRappelFin;
  }

  public TypeHydrantIndispoStatutRecord getStatut() {
    return statut;
  }

  public void setStatut(TypeHydrantIndispoStatutRecord statut) {
    this.statut = statut;
  }

  public Integer getTotalHydrants() {
    return totalHydrants;
  }

  public void setTotalHydrants(Integer totalHydrants) {
    this.totalHydrants = totalHydrants;
  }

  public boolean isBasculeAutoIndispo() {
    return basculeAutoIndispo;
  }

  public void setBasculeAutoIndispo(boolean basculeAutoIndispo) {
    this.basculeAutoIndispo = basculeAutoIndispo;
  }

  public boolean isBasculeAutoDispo() {
    return basculeAutoDispo;
  }

  public void setBasculeAutoDispo(boolean basculeAutoDispo) {
    this.basculeAutoDispo = basculeAutoDispo;
  }

  public boolean isMailAvantIndispo() {
    return mailAvantIndispo;
  }

  public void setMailAvantIndispo(boolean mailAvantIndispo) {
    this.mailAvantIndispo = mailAvantIndispo;
  }

  public boolean isMailAvantDispo() {
    return mailAvantDispo;
  }

  public void setMailAvantDispo(boolean mailAvantDispo) {
    this.mailAvantDispo = mailAvantDispo;
  }

  public String getObservation() {
    return observation;
  }

  public void setObservation(String observation) {
    this.observation = observation;
  }

  public String getCommune() {
    return commune;
  }

  public void setCommune(String commune) {
    this.commune = commune;
  }

  public List<HydrantRecord> getHydrants() {
    return hydrants;
  }

  public void setHydrants(List<HydrantRecord> hydrants) {
    this.hydrants = hydrants;
  }

  public Geometry getGeometrie() {
    return geometrie;
  }

  public void setGeometrie(Geometry geometrie) {
    this.geometrie = geometrie;
  }

  public String getHydrantsTooltip() {
    return hydrantsTooltip;
  }

  public void setHydrantsTooltip(String hydrantsTooltip) {
    this.hydrantsTooltip = hydrantsTooltip;
  }
}
