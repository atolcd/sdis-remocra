/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Profil de droit autorisé pour executer une requête
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RequeteModeleDroit implements Serializable {

    private static final long serialVersionUID = -2118763981;

    private Long requeteModele;
    private Long profilDroit;

    public RequeteModeleDroit() {}

    public RequeteModeleDroit(RequeteModeleDroit value) {
        this.requeteModele = value.requeteModele;
        this.profilDroit = value.profilDroit;
    }

    public RequeteModeleDroit(
        Long requeteModele,
        Long profilDroit
    ) {
        this.requeteModele = requeteModele;
        this.profilDroit = profilDroit;
    }

    public Long getRequeteModele() {
        return this.requeteModele;
    }

    public RequeteModeleDroit setRequeteModele(Long requeteModele) {
        this.requeteModele = requeteModele;
        return this;
    }

    public Long getProfilDroit() {
        return this.profilDroit;
    }

    public RequeteModeleDroit setProfilDroit(Long profilDroit) {
        this.profilDroit = profilDroit;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RequeteModeleDroit (");

        sb.append(requeteModele);
        sb.append(", ").append(profilDroit);

        sb.append(")");
        return sb.toString();
    }
}
