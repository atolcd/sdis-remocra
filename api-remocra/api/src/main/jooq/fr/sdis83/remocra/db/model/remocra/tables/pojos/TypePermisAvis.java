/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypePermisAvis implements Serializable {

    private static final long serialVersionUID = -1980337491;

    private Long    id;
    private Boolean actif;
    private String  code;
    private String  nom;
    private Boolean pprif;

    public TypePermisAvis() {}

    public TypePermisAvis(TypePermisAvis value) {
        this.id = value.id;
        this.actif = value.actif;
        this.code = value.code;
        this.nom = value.nom;
        this.pprif = value.pprif;
    }

    public TypePermisAvis(
        Long    id,
        Boolean actif,
        String  code,
        String  nom,
        Boolean pprif
    ) {
        this.id = id;
        this.actif = actif;
        this.code = code;
        this.nom = nom;
        this.pprif = pprif;
    }

    public Long getId() {
        return this.id;
    }

    public TypePermisAvis setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public TypePermisAvis setActif(Boolean actif) {
        this.actif = actif;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public TypePermisAvis setCode(String code) {
        this.code = code;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public TypePermisAvis setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Boolean getPprif() {
        return this.pprif;
    }

    public TypePermisAvis setPprif(Boolean pprif) {
        this.pprif = pprif;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypePermisAvis (");

        sb.append(id);
        sb.append(", ").append(actif);
        sb.append(", ").append(code);
        sb.append(", ").append(nom);
        sb.append(", ").append(pprif);

        sb.append(")");
        return sb.toString();
    }
}
