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
public class Contact implements Serializable {

    private static final long serialVersionUID = 923405446;

    private Long   id;
    private String appartenance;
    private String idAppartenance;
    private String fonction;
    private String civilite;
    private String nom;
    private String prenom;
    private String numeroVoie;
    private String suffixeVoie;
    private String lieuDit;
    private String voie;
    private String codePostal;
    private String ville;
    private String pays;
    private String telephone;
    private String email;

    public Contact() {}

    public Contact(Contact value) {
        this.id = value.id;
        this.appartenance = value.appartenance;
        this.idAppartenance = value.idAppartenance;
        this.fonction = value.fonction;
        this.civilite = value.civilite;
        this.nom = value.nom;
        this.prenom = value.prenom;
        this.numeroVoie = value.numeroVoie;
        this.suffixeVoie = value.suffixeVoie;
        this.lieuDit = value.lieuDit;
        this.voie = value.voie;
        this.codePostal = value.codePostal;
        this.ville = value.ville;
        this.pays = value.pays;
        this.telephone = value.telephone;
        this.email = value.email;
    }

    public Contact(
        Long   id,
        String appartenance,
        String idAppartenance,
        String fonction,
        String civilite,
        String nom,
        String prenom,
        String numeroVoie,
        String suffixeVoie,
        String lieuDit,
        String voie,
        String codePostal,
        String ville,
        String pays,
        String telephone,
        String email
    ) {
        this.id = id;
        this.appartenance = appartenance;
        this.idAppartenance = idAppartenance;
        this.fonction = fonction;
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroVoie = numeroVoie;
        this.suffixeVoie = suffixeVoie;
        this.lieuDit = lieuDit;
        this.voie = voie;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
        this.telephone = telephone;
        this.email = email;
    }

    public Long getId() {
        return this.id;
    }

    public Contact setId(Long id) {
        this.id = id;
        return this;
    }

    public String getAppartenance() {
        return this.appartenance;
    }

    public Contact setAppartenance(String appartenance) {
        this.appartenance = appartenance;
        return this;
    }

    public String getIdAppartenance() {
        return this.idAppartenance;
    }

    public Contact setIdAppartenance(String idAppartenance) {
        this.idAppartenance = idAppartenance;
        return this;
    }

    public String getFonction() {
        return this.fonction;
    }

    public Contact setFonction(String fonction) {
        this.fonction = fonction;
        return this;
    }

    public String getCivilite() {
        return this.civilite;
    }

    public Contact setCivilite(String civilite) {
        this.civilite = civilite;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public Contact setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Contact setPrenom(String prenom) {
        this.prenom = prenom;
        return this;
    }

    public String getNumeroVoie() {
        return this.numeroVoie;
    }

    public Contact setNumeroVoie(String numeroVoie) {
        this.numeroVoie = numeroVoie;
        return this;
    }

    public String getSuffixeVoie() {
        return this.suffixeVoie;
    }

    public Contact setSuffixeVoie(String suffixeVoie) {
        this.suffixeVoie = suffixeVoie;
        return this;
    }

    public String getLieuDit() {
        return this.lieuDit;
    }

    public Contact setLieuDit(String lieuDit) {
        this.lieuDit = lieuDit;
        return this;
    }

    public String getVoie() {
        return this.voie;
    }

    public Contact setVoie(String voie) {
        this.voie = voie;
        return this;
    }

    public String getCodePostal() {
        return this.codePostal;
    }

    public Contact setCodePostal(String codePostal) {
        this.codePostal = codePostal;
        return this;
    }

    public String getVille() {
        return this.ville;
    }

    public Contact setVille(String ville) {
        this.ville = ville;
        return this;
    }

    public String getPays() {
        return this.pays;
    }

    public Contact setPays(String pays) {
        this.pays = pays;
        return this;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Contact setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public Contact setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Contact (");

        sb.append(id);
        sb.append(", ").append(appartenance);
        sb.append(", ").append(idAppartenance);
        sb.append(", ").append(fonction);
        sb.append(", ").append(civilite);
        sb.append(", ").append(nom);
        sb.append(", ").append(prenom);
        sb.append(", ").append(numeroVoie);
        sb.append(", ").append(suffixeVoie);
        sb.append(", ").append(lieuDit);
        sb.append(", ").append(voie);
        sb.append(", ").append(codePostal);
        sb.append(", ").append(ville);
        sb.append(", ").append(pays);
        sb.append(", ").append(telephone);
        sb.append(", ").append(email);

        sb.append(")");
        return sb.toString();
    }
}