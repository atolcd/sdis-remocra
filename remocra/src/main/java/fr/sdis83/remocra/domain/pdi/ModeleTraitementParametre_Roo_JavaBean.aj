// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.pdi;

import fr.sdis83.remocra.domain.pdi.ModeleTraitementParametre;
import fr.sdis83.remocra.domain.pdi.TraitementParametre;
import java.util.Set;

privileged aspect ModeleTraitementParametre_Roo_JavaBean {
    
    public Set<TraitementParametre> ModeleTraitementParametre.getTraitementParametres() {
        return this.traitementParametres;
    }
    
    public void ModeleTraitementParametre.setTraitementParametres(Set<TraitementParametre> traitementParametres) {
        this.traitementParametres = traitementParametres;
    }
    
}
