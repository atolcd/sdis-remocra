// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import java.util.Set;

privileged aspect TypeOldebAvis_Roo_JavaBean {

    public Long TypeOldebAvis.getId() {
        return this.id;
    }

    public void TypeOldebAvis.setId(Long id) {
        this.id = id;
    }

    public Set<OldebVisite> TypeOldebAvis.getOldebVisites() {
        return oldebVisites;
    }

    public void TypeOldebAvis.setOldebVisites(Set<OldebVisite> oldebVisites) {
        this.oldebVisites = oldebVisites;
    }

    public Boolean TypeOldebAvis.getActif() {
        return actif;
    }

    public void TypeOldebAvis.setActif(Boolean actif) {
        this.actif = actif;
    }

    public String TypeOldebAvis.getCode() {
        return code;
    }

    public void TypeOldebAvis.setCode(String code) {
        this.code = code;
    }

    public String TypeOldebAvis.getNom() {
        return nom;
    }

    public void TypeOldebAvis.setNom(String nom) {
        this.nom = nom;
    }

}
