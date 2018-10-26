package fr.sdis83.remocra.web.model;

import java.util.List;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanificationParametre;

public class ProcessusEtlPlanification {


  private Long id;

  private Long modele;

  private String expression;

  private String categorie;

  private Long objetConcerne;

  private List<ProcessusEtlPlanificationParametre> processusEtlPlanificationParametres;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getModele() {
    return modele;
  }

  public void setModele(Long modele) {
    this.modele = modele;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getCategorie() {
    return categorie;
  }

  public void setCategorie(String categorie) {
    this.categorie = categorie;
  }

  public Long getObjetConcerne() {
    return objetConcerne;
  }

  public void setObjetConcerne(Long objetConcerne) {
    this.objetConcerne = objetConcerne;
  }

  public List<ProcessusEtlPlanificationParametre> getProcessusEtlPlanificationParametres() {
    return processusEtlPlanificationParametres;
  }

  public void setProcessusEtlPlanificationParametres(List<ProcessusEtlPlanificationParametre> processusEtlPlanificationParametres) {
    this.processusEtlPlanificationParametres = processusEtlPlanificationParametres;
  }
}
