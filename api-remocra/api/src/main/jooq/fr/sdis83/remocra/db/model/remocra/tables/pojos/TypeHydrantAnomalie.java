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
public class TypeHydrantAnomalie implements Serializable {

    private static final long serialVersionUID = -1277085493;

    private Long    id;
    private Boolean actif;
    private String  code;
    private String  commentaire;
    private String  nom;
    private Integer version;
    private Long    critere;

    public TypeHydrantAnomalie() {}

    public TypeHydrantAnomalie(TypeHydrantAnomalie value) {
        this.id = value.id;
        this.actif = value.actif;
        this.code = value.code;
        this.commentaire = value.commentaire;
        this.nom = value.nom;
        this.version = value.version;
        this.critere = value.critere;
    }

    public TypeHydrantAnomalie(
        Long    id,
        Boolean actif,
        String  code,
        String  commentaire,
        String  nom,
        Integer version,
        Long    critere
    ) {
        this.id = id;
        this.actif = actif;
        this.code = code;
        this.commentaire = commentaire;
        this.nom = nom;
        this.version = version;
        this.critere = critere;
    }

    public Long getId() {
        return this.id;
    }

    public TypeHydrantAnomalie setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public TypeHydrantAnomalie setActif(Boolean actif) {
        this.actif = actif;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public TypeHydrantAnomalie setCode(String code) {
        this.code = code;
        return this;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public TypeHydrantAnomalie setCommentaire(String commentaire) {
        this.commentaire = commentaire;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public TypeHydrantAnomalie setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Integer getVersion() {
        return this.version;
    }

    public TypeHydrantAnomalie setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Long getCritere() {
        return this.critere;
    }

    public TypeHydrantAnomalie setCritere(Long critere) {
        this.critere = critere;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypeHydrantAnomalie (");

        sb.append(id);
        sb.append(", ").append(actif);
        sb.append(", ").append(code);
        sb.append(", ").append(commentaire);
        sb.append(", ").append(nom);
        sb.append(", ").append(version);
        sb.append(", ").append(critere);

        sb.append(")");
        return sb.toString();
    }
}
