/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Gestionnaire de PEI
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Gestionnaire implements Serializable {

    private static final long serialVersionUID = 988445196;

    private Long    id;
    private Boolean actif;
    private String  code;
    private String  nom;
    private Integer version;

    public Gestionnaire() {}

    public Gestionnaire(Gestionnaire value) {
        this.id = value.id;
        this.actif = value.actif;
        this.code = value.code;
        this.nom = value.nom;
        this.version = value.version;
    }

    public Gestionnaire(
        Long    id,
        Boolean actif,
        String  code,
        String  nom,
        Integer version
    ) {
        this.id = id;
        this.actif = actif;
        this.code = code;
        this.nom = nom;
        this.version = version;
    }

    public Long getId() {
        return this.id;
    }

    public Gestionnaire setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public Gestionnaire setActif(Boolean actif) {
        this.actif = actif;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public Gestionnaire setCode(String code) {
        this.code = code;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public Gestionnaire setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Gestionnaire setVersion(Integer version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Gestionnaire (");

        sb.append(id);
        sb.append(", ").append(actif);
        sb.append(", ").append(code);
        sb.append(", ").append(nom);
        sb.append(", ").append(version);

        sb.append(")");
        return sb.toString();
    }
}