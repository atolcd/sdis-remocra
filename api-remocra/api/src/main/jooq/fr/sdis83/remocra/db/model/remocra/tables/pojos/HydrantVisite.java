/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;
import java.time.Instant;

import javax.annotation.Generated;


/**
 * Visite d'un hydrant
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HydrantVisite implements Serializable {

    private static final long serialVersionUID = 1023331961;

    private Long    id;
    private Long    hydrant;
    private Instant date;
    private Long    type;
    private Boolean ctrlDebitPression;
    private String  agent1;
    private String  agent2;
    private Integer debit;
    private Integer debitMax;
    private Double  pression;
    private Double  pressionDyn;
    private Double  pressionDynDeb;
    private String  anomalies;
    private String  observations;
    private Long    utilisateurModification;
    private Long    organisme;
    private String  auteurModificationFlag;

    public HydrantVisite() {}

    public HydrantVisite(HydrantVisite value) {
        this.id = value.id;
        this.hydrant = value.hydrant;
        this.date = value.date;
        this.type = value.type;
        this.ctrlDebitPression = value.ctrlDebitPression;
        this.agent1 = value.agent1;
        this.agent2 = value.agent2;
        this.debit = value.debit;
        this.debitMax = value.debitMax;
        this.pression = value.pression;
        this.pressionDyn = value.pressionDyn;
        this.pressionDynDeb = value.pressionDynDeb;
        this.anomalies = value.anomalies;
        this.observations = value.observations;
        this.utilisateurModification = value.utilisateurModification;
        this.organisme = value.organisme;
        this.auteurModificationFlag = value.auteurModificationFlag;
    }

    public HydrantVisite(
        Long    id,
        Long    hydrant,
        Instant date,
        Long    type,
        Boolean ctrlDebitPression,
        String  agent1,
        String  agent2,
        Integer debit,
        Integer debitMax,
        Double  pression,
        Double  pressionDyn,
        Double  pressionDynDeb,
        String  anomalies,
        String  observations,
        Long    utilisateurModification,
        Long    organisme,
        String  auteurModificationFlag
    ) {
        this.id = id;
        this.hydrant = hydrant;
        this.date = date;
        this.type = type;
        this.ctrlDebitPression = ctrlDebitPression;
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.debit = debit;
        this.debitMax = debitMax;
        this.pression = pression;
        this.pressionDyn = pressionDyn;
        this.pressionDynDeb = pressionDynDeb;
        this.anomalies = anomalies;
        this.observations = observations;
        this.utilisateurModification = utilisateurModification;
        this.organisme = organisme;
        this.auteurModificationFlag = auteurModificationFlag;
    }

    public Long getId() {
        return this.id;
    }

    public HydrantVisite setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getHydrant() {
        return this.hydrant;
    }

    public HydrantVisite setHydrant(Long hydrant) {
        this.hydrant = hydrant;
        return this;
    }

    public Instant getDate() {
        return this.date;
    }

    public HydrantVisite setDate(Instant date) {
        this.date = date;
        return this;
    }

    public Long getType() {
        return this.type;
    }

    public HydrantVisite setType(Long type) {
        this.type = type;
        return this;
    }

    public Boolean getCtrlDebitPression() {
        return this.ctrlDebitPression;
    }

    public HydrantVisite setCtrlDebitPression(Boolean ctrlDebitPression) {
        this.ctrlDebitPression = ctrlDebitPression;
        return this;
    }

    public String getAgent1() {
        return this.agent1;
    }

    public HydrantVisite setAgent1(String agent1) {
        this.agent1 = agent1;
        return this;
    }

    public String getAgent2() {
        return this.agent2;
    }

    public HydrantVisite setAgent2(String agent2) {
        this.agent2 = agent2;
        return this;
    }

    public Integer getDebit() {
        return this.debit;
    }

    public HydrantVisite setDebit(Integer debit) {
        this.debit = debit;
        return this;
    }

    public Integer getDebitMax() {
        return this.debitMax;
    }

    public HydrantVisite setDebitMax(Integer debitMax) {
        this.debitMax = debitMax;
        return this;
    }

    public Double getPression() {
        return this.pression;
    }

    public HydrantVisite setPression(Double pression) {
        this.pression = pression;
        return this;
    }

    public Double getPressionDyn() {
        return this.pressionDyn;
    }

    public HydrantVisite setPressionDyn(Double pressionDyn) {
        this.pressionDyn = pressionDyn;
        return this;
    }

    public Double getPressionDynDeb() {
        return this.pressionDynDeb;
    }

    public HydrantVisite setPressionDynDeb(Double pressionDynDeb) {
        this.pressionDynDeb = pressionDynDeb;
        return this;
    }

    public String getAnomalies() {
        return this.anomalies;
    }

    public HydrantVisite setAnomalies(String anomalies) {
        this.anomalies = anomalies;
        return this;
    }

    public String getObservations() {
        return this.observations;
    }

    public HydrantVisite setObservations(String observations) {
        this.observations = observations;
        return this;
    }

    public Long getUtilisateurModification() {
        return this.utilisateurModification;
    }

    public HydrantVisite setUtilisateurModification(Long utilisateurModification) {
        this.utilisateurModification = utilisateurModification;
        return this;
    }

    public Long getOrganisme() {
        return this.organisme;
    }

    public HydrantVisite setOrganisme(Long organisme) {
        this.organisme = organisme;
        return this;
    }

    public String getAuteurModificationFlag() {
        return this.auteurModificationFlag;
    }

    public HydrantVisite setAuteurModificationFlag(String auteurModificationFlag) {
        this.auteurModificationFlag = auteurModificationFlag;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HydrantVisite (");

        sb.append(id);
        sb.append(", ").append(hydrant);
        sb.append(", ").append(date);
        sb.append(", ").append(type);
        sb.append(", ").append(ctrlDebitPression);
        sb.append(", ").append(agent1);
        sb.append(", ").append(agent2);
        sb.append(", ").append(debit);
        sb.append(", ").append(debitMax);
        sb.append(", ").append(pression);
        sb.append(", ").append(pressionDyn);
        sb.append(", ").append(pressionDynDeb);
        sb.append(", ").append(anomalies);
        sb.append(", ").append(observations);
        sb.append(", ").append(utilisateurModification);
        sb.append(", ").append(organisme);
        sb.append(", ").append(auteurModificationFlag);

        sb.append(")");
        return sb.toString();
    }
}