/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Profil de droit autorisé pour générer un courrier
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CourrierModeleDroit implements Serializable {

    private static final long serialVersionUID = -1219817563;

    private Long modele;
    private Long profilDroit;

    public CourrierModeleDroit() {}

    public CourrierModeleDroit(CourrierModeleDroit value) {
        this.modele = value.modele;
        this.profilDroit = value.profilDroit;
    }

    public CourrierModeleDroit(
        Long modele,
        Long profilDroit
    ) {
        this.modele = modele;
        this.profilDroit = profilDroit;
    }

    public Long getModele() {
        return this.modele;
    }

    public CourrierModeleDroit setModele(Long modele) {
        this.modele = modele;
        return this;
    }

    public Long getProfilDroit() {
        return this.profilDroit;
    }

    public CourrierModeleDroit setProfilDroit(Long profilDroit) {
        this.profilDroit = profilDroit;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CourrierModeleDroit (");

        sb.append(modele);
        sb.append(", ").append(profilDroit);

        sb.append(")");
        return sb.toString();
    }
}