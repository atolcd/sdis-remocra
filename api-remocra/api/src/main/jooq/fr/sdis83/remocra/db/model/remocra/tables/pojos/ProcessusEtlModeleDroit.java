/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Profil de droit autorisé pour exécuter un processus ETL
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProcessusEtlModeleDroit implements Serializable {

    private static final long serialVersionUID = 1903233945;

    private Long modele;
    private Long profilDroit;

    public ProcessusEtlModeleDroit() {}

    public ProcessusEtlModeleDroit(ProcessusEtlModeleDroit value) {
        this.modele = value.modele;
        this.profilDroit = value.profilDroit;
    }

    public ProcessusEtlModeleDroit(
        Long modele,
        Long profilDroit
    ) {
        this.modele = modele;
        this.profilDroit = profilDroit;
    }

    public Long getModele() {
        return this.modele;
    }

    public ProcessusEtlModeleDroit setModele(Long modele) {
        this.modele = modele;
        return this;
    }

    public Long getProfilDroit() {
        return this.profilDroit;
    }

    public ProcessusEtlModeleDroit setProfilDroit(Long profilDroit) {
        this.profilDroit = profilDroit;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProcessusEtlModeleDroit (");

        sb.append(modele);
        sb.append(", ").append(profilDroit);

        sb.append(")");
        return sb.toString();
    }
}
