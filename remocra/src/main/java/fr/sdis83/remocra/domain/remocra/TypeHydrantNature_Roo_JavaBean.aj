// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeHydrant;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;

privileged aspect TypeHydrantNature_Roo_JavaBean {
    
    public Long TypeHydrantNature.getId() {
        return this.id;
    }
    
    public void TypeHydrantNature.setId(Long id) {
        this.id = id;
    }
    
    public String TypeHydrantNature.getNom() {
        return this.nom;
    }
    
    public void TypeHydrantNature.setNom(String nom) {
        this.nom = nom;
    }
    
    public String TypeHydrantNature.getCode() {
        return this.code;
    }
    
    public void TypeHydrantNature.setCode(String code) {
        this.code = code;
    }
    
    public Boolean TypeHydrantNature.getActif() {
        return this.actif;
    }
    
    public void TypeHydrantNature.setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public TypeHydrant TypeHydrantNature.getTypeHydrant() {
        return this.typeHydrant;
    }
    
    public void TypeHydrantNature.setTypeHydrant(TypeHydrant typeHydrant) {
        this.typeHydrant = typeHydrant;
    }
    
}
