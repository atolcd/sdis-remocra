/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables.pojos;


import java.io.Serializable;
import java.time.Instant;

import javax.annotation.Generated;


/**
 * Requêtes personnalisées lancées par utilisateur dans REMOCRA
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RequeteModeleSelection implements Serializable {

    private static final long serialVersionUID = -1147497125;

    private Long    id;
    private String  requete;
    private Long    modele;
    private Long    utilisateur;
    private Instant date;
    private Object  etendu;

    public RequeteModeleSelection() {}

    public RequeteModeleSelection(RequeteModeleSelection value) {
        this.id = value.id;
        this.requete = value.requete;
        this.modele = value.modele;
        this.utilisateur = value.utilisateur;
        this.date = value.date;
        this.etendu = value.etendu;
    }

    public RequeteModeleSelection(
        Long    id,
        String  requete,
        Long    modele,
        Long    utilisateur,
        Instant date,
        Object  etendu
    ) {
        this.id = id;
        this.requete = requete;
        this.modele = modele;
        this.utilisateur = utilisateur;
        this.date = date;
        this.etendu = etendu;
    }

    public Long getId() {
        return this.id;
    }

    public RequeteModeleSelection setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRequete() {
        return this.requete;
    }

    public RequeteModeleSelection setRequete(String requete) {
        this.requete = requete;
        return this;
    }

    public Long getModele() {
        return this.modele;
    }

    public RequeteModeleSelection setModele(Long modele) {
        this.modele = modele;
        return this;
    }

    public Long getUtilisateur() {
        return this.utilisateur;
    }

    public RequeteModeleSelection setUtilisateur(Long utilisateur) {
        this.utilisateur = utilisateur;
        return this;
    }

    public Instant getDate() {
        return this.date;
    }

    public RequeteModeleSelection setDate(Instant date) {
        this.date = date;
        return this;
    }


    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public Object getEtendu() {
        return this.etendu;
    }


    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public RequeteModeleSelection setEtendu(Object etendu) {
        this.etendu = etendu;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RequeteModeleSelection (");

        sb.append(id);
        sb.append(", ").append(requete);
        sb.append(", ").append(modele);
        sb.append(", ").append(utilisateur);
        sb.append(", ").append(date);
        sb.append(", ").append(etendu);

        sb.append(")");
        return sb.toString();
    }
}