// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.pdi;

import fr.sdis83.remocra.domain.pdi.Traitement;
import fr.sdis83.remocra.domain.pdi.TraitementCc;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

privileged aspect TraitementCc_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumn(name = "idtraitement", referencedColumnName = "idtraitement", nullable = false, insertable = false, updatable = false)
    private Traitement TraitementCc.idtraitement;
    
    public Traitement TraitementCc.getIdtraitement() {
        return idtraitement;
    }
    
    public void TraitementCc.setIdtraitement(Traitement idtraitement) {
        this.idtraitement = idtraitement;
    }
    
}
