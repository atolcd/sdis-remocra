/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;
import java.time.Instant;

import javax.annotation.Generated;


/**
 * Gestion de crise : Episode de crise pour lequel des informations ont été 
 * saisies dans REMOCRA
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Crise implements Serializable {

    private static final long serialVersionUID = 203679239;

    private Long    id;
    private String  nom;
    private String  description;
    private Instant activation;
    private Instant cloture;
    private Long    statut;
    private Long    typeCrise;
    private String  carteAnt;
    private Instant redefinition;
    private Long    criseParente;
    private Long    auteurCrise;
    private String  carteOp;

    public Crise() {}

    public Crise(Crise value) {
        this.id = value.id;
        this.nom = value.nom;
        this.description = value.description;
        this.activation = value.activation;
        this.cloture = value.cloture;
        this.statut = value.statut;
        this.typeCrise = value.typeCrise;
        this.carteAnt = value.carteAnt;
        this.redefinition = value.redefinition;
        this.criseParente = value.criseParente;
        this.auteurCrise = value.auteurCrise;
        this.carteOp = value.carteOp;
    }

    public Crise(
        Long    id,
        String  nom,
        String  description,
        Instant activation,
        Instant cloture,
        Long    statut,
        Long    typeCrise,
        String  carteAnt,
        Instant redefinition,
        Long    criseParente,
        Long    auteurCrise,
        String  carteOp
    ) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.activation = activation;
        this.cloture = cloture;
        this.statut = statut;
        this.typeCrise = typeCrise;
        this.carteAnt = carteAnt;
        this.redefinition = redefinition;
        this.criseParente = criseParente;
        this.auteurCrise = auteurCrise;
        this.carteOp = carteOp;
    }

    public Long getId() {
        return this.id;
    }

    public Crise setId(Long id) {
        this.id = id;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public Crise setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Crise setDescription(String description) {
        this.description = description;
        return this;
    }

    public Instant getActivation() {
        return this.activation;
    }

    public Crise setActivation(Instant activation) {
        this.activation = activation;
        return this;
    }

    public Instant getCloture() {
        return this.cloture;
    }

    public Crise setCloture(Instant cloture) {
        this.cloture = cloture;
        return this;
    }

    public Long getStatut() {
        return this.statut;
    }

    public Crise setStatut(Long statut) {
        this.statut = statut;
        return this;
    }

    public Long getTypeCrise() {
        return this.typeCrise;
    }

    public Crise setTypeCrise(Long typeCrise) {
        this.typeCrise = typeCrise;
        return this;
    }

    public String getCarteAnt() {
        return this.carteAnt;
    }

    public Crise setCarteAnt(String carteAnt) {
        this.carteAnt = carteAnt;
        return this;
    }

    public Instant getRedefinition() {
        return this.redefinition;
    }

    public Crise setRedefinition(Instant redefinition) {
        this.redefinition = redefinition;
        return this;
    }

    public Long getCriseParente() {
        return this.criseParente;
    }

    public Crise setCriseParente(Long criseParente) {
        this.criseParente = criseParente;
        return this;
    }

    public Long getAuteurCrise() {
        return this.auteurCrise;
    }

    public Crise setAuteurCrise(Long auteurCrise) {
        this.auteurCrise = auteurCrise;
        return this;
    }

    public String getCarteOp() {
        return this.carteOp;
    }

    public Crise setCarteOp(String carteOp) {
        this.carteOp = carteOp;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Crise (");

        sb.append(id);
        sb.append(", ").append(nom);
        sb.append(", ").append(description);
        sb.append(", ").append(activation);
        sb.append(", ").append(cloture);
        sb.append(", ").append(statut);
        sb.append(", ").append(typeCrise);
        sb.append(", ").append(carteAnt);
        sb.append(", ").append(redefinition);
        sb.append(", ").append(criseParente);
        sb.append(", ").append(auteurCrise);
        sb.append(", ").append(carteOp);

        sb.append(")");
        return sb.toString();
    }
}