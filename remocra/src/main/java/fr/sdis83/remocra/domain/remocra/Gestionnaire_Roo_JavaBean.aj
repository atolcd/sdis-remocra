// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.Gestionnaire;
import java.util.Set;

privileged aspect Gestionnaire_Roo_JavaBean {
    
    public Long Gestionnaire.getId() {
        return this.id;
    }
    
    public void Gestionnaire.setId(Long id) {
        this.id = id;
    }
    
    public Integer Gestionnaire.getVersion() {
        return this.version;
    }
    
    public void Gestionnaire.setVersion(Integer version) {
        this.version = version;
    }
    
    public String Gestionnaire.getNom() {
        return this.nom;
    }
    
    public void Gestionnaire.setNom(String nom) {
        this.nom = nom;
    }
    
    public String Gestionnaire.getCode() {
        return this.code;
    }
    
    public void Gestionnaire.setCode(String code) {
        this.code = code;
    }
    
    public Boolean Gestionnaire.getActif() {
        return this.actif;
    }
    
    public void Gestionnaire.setActif(Boolean actif) {
        this.actif = actif;
    }
    
}