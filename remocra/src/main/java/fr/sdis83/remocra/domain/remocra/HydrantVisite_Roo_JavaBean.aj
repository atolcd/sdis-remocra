// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.HydrantVisite;
import java.util.Set;
import java.util.Date;

privileged aspect HydrantVisite_Roo_JavaBean {
    
    public Long HydrantVisite.getId() {
        return this.id;
    }
    
    public void HydrantVisite.setId(Long id) {
        this.id = id;
    }

    public Hydrant HydrantVisite.getHydrant() {
        return this.hydrant;
    }

    public void HydrantVisite.setHydrant(Hydrant hydrant) {
        this.hydrant = hydrant;
    }
    
    public Date HydrantVisite.getDate() {
        return this.date;
    }
    
    public void HydrantVisite.setDate(Date date) {
        this.date = date;
    }
    
    public TypeHydrantSaisie HydrantVisite.getType() {
        return this.type;
    }
    
    public void HydrantVisite.setType(TypeHydrantSaisie type) {
        this.type = type;
    }
    
    public Boolean HydrantVisite.getCtrl_debit_pression() {
        return this.ctrl_debit_pression;
    }
    
    public void HydrantVisite.setCtrl_debit_pression(Boolean ctrl_debit_pression) {
        this.ctrl_debit_pression = ctrl_debit_pression;
    }

    public String HydrantVisite.getAgent1() {
        return this.agent1;
    }

    public void HydrantVisite.setAgent1(String agent1) {
        this.agent1 = agent1;
    }

    public String HydrantVisite.getAgent2() {
        return this.agent2;
    }

    public void HydrantVisite.setAgent2(String agent2) {
        this.agent2 = agent2;
    }

    public Integer HydrantVisite.getDebit() {
        return this.debit;
    }

    public void HydrantVisite.setDebit(Integer debit) {
        this.debit = debit;
    }

    public Integer HydrantVisite.getDebitMax() {
        return this.debitMax;
    }

    public void HydrantVisite.setDebitMax(Integer debitMax) {
        this.debitMax = debitMax;
    }

    public Double HydrantVisite.getPression() {
        return this.pression;
    }

    public void HydrantVisite.setPression(Double pression) {
        this.pression = pression;
    }

    public Double HydrantVisite.getPressionDyn() {
        return this.pressionDyn;
    }

    public void HydrantVisite.setPressionDyn(Double pressionDyn) {
        this.pressionDyn = pressionDyn;
    }

    public Double HydrantVisite.getPressionDynDeb() {
        return this.pressionDynDeb;
    }

    public void HydrantVisite.setPressionDynDeb(Double pressionDynDeb) {
        this.pressionDynDeb = pressionDynDeb;
    }

    public String HydrantVisite.getAnomalies() {
        return this.anomalies;
    }

    public void HydrantVisite.setAnomalies(String anomalies) {
        this.anomalies = anomalies;
    }

    public String HydrantVisite.getObservations() {
        return this.observations;
    }

    public void HydrantVisite.setObservations(String observations) {
        this.observations = observations;
    }

    
}