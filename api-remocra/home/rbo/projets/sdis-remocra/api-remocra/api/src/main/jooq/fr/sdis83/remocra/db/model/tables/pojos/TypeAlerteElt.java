/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables.pojos;


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
public class TypeAlerteElt implements Serializable {

    private static final long serialVersionUID = -2037902811;

    private Long    id;
    private Boolean actif;
    private String  code;
    private String  nom;

    public TypeAlerteElt() {}

    public TypeAlerteElt(TypeAlerteElt value) {
        this.id = value.id;
        this.actif = value.actif;
        this.code = value.code;
        this.nom = value.nom;
    }

    public TypeAlerteElt(
        Long    id,
        Boolean actif,
        String  code,
        String  nom
    ) {
        this.id = id;
        this.actif = actif;
        this.code = code;
        this.nom = nom;
    }

    public Long getId() {
        return this.id;
    }

    public TypeAlerteElt setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public TypeAlerteElt setActif(Boolean actif) {
        this.actif = actif;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public TypeAlerteElt setCode(String code) {
        this.code = code;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public TypeAlerteElt setNom(String nom) {
        this.nom = nom;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypeAlerteElt (");

        sb.append(id);
        sb.append(", ").append(actif);
        sb.append(", ").append(code);
        sb.append(", ").append(nom);

        sb.append(")");
        return sb.toString();
    }
}