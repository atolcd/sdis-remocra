// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.Etude;
import java.util.Set;
import java.util.Date;

privileged aspect Etude_Roo_JavaBean {
    
    public Long Etude.getId() {
        return this.id;
    }
    
    public void Etude.setId(Long id) {
        this.id = id;
    }

    public TypeEtude Etude.getType() {
        return this.type;
    }

    public void Etude.setType(TypeEtude type) {
        this.type = type;
    }
    
    public Date Etude.getDateMiseAJour() {
        return this.dateMiseAJour;
    }
    
    public void Etude.setDateMiseAJour(Date dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }
    
    public TypeEtudeStatut Etude.getStatut() {
        return this.statut;
    }
    
    public void Etude.setStatut(TypeEtudeStatut statut) {
        this.statut = statut;
    }


    public String Etude.getNom() {
        return this.nom;
    }

    public void Etude.setNom(String nom) {
        this.nom = nom;
    }

    public String Etude.getDescription() {
        return this.description;
    }

    public void Etude.setDescription(String description) {
        this.description = description;
    }

    public String Etude.getNumero() {
        return this.numero;
    }

    public void Etude.setNumero(String numero) {
        this.numero = numero;
    }
    
}