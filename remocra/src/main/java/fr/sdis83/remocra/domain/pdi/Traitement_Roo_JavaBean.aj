// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.pdi;

import fr.sdis83.remocra.domain.pdi.Traitement;
import fr.sdis83.remocra.domain.pdi.TraitementParametre;
import java.util.Date;
import java.util.Set;

privileged aspect Traitement_Roo_JavaBean {
    
    public Integer Traitement.getIdtraitement() {
        return this.idtraitement;
    }
    
    public void Traitement.setIdtraitement(Integer idtraitement) {
        this.idtraitement = idtraitement;
    }
    
    public Date Traitement.getDemande() {
        return this.demande;
    }
    
    public void Traitement.setDemande(Date demande) {
        this.demande = demande;
    }
    
    public Date Traitement.getExecution() {
        return this.execution;
    }
    
    public void Traitement.setExecution(Date execution) {
        this.execution = execution;
    }
    
    public Set<TraitementParametre> Traitement.getTraitementParametres() {
        return this.traitementParametres;
    }
    
    public void Traitement.setTraitementParametres(Set<TraitementParametre> traitementParametres) {
        this.traitementParametres = traitementParametres;
    }
    
}
