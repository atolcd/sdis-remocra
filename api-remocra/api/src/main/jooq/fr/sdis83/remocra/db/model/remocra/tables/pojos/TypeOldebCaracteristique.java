/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Caractéristique permettant de qualifier une parcelle soumise à une obligation 
 * légale de débroussaillement
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeOldebCaracteristique implements Serializable {

    private static final long serialVersionUID = -1087438678;

    private Long    id;
    private Boolean actif;
    private String  code;
    private String  nom;
    private Long    categorie;

    public TypeOldebCaracteristique() {}

    public TypeOldebCaracteristique(TypeOldebCaracteristique value) {
        this.id = value.id;
        this.actif = value.actif;
        this.code = value.code;
        this.nom = value.nom;
        this.categorie = value.categorie;
    }

    public TypeOldebCaracteristique(
        Long    id,
        Boolean actif,
        String  code,
        String  nom,
        Long    categorie
    ) {
        this.id = id;
        this.actif = actif;
        this.code = code;
        this.nom = nom;
        this.categorie = categorie;
    }

    public Long getId() {
        return this.id;
    }

    public TypeOldebCaracteristique setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public TypeOldebCaracteristique setActif(Boolean actif) {
        this.actif = actif;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public TypeOldebCaracteristique setCode(String code) {
        this.code = code;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public TypeOldebCaracteristique setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Long getCategorie() {
        return this.categorie;
    }

    public TypeOldebCaracteristique setCategorie(Long categorie) {
        this.categorie = categorie;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypeOldebCaracteristique (");

        sb.append(id);
        sb.append(", ").append(actif);
        sb.append(", ").append(code);
        sb.append(", ").append(nom);
        sb.append(", ").append(categorie);

        sb.append(")");
        return sb.toString();
    }
}