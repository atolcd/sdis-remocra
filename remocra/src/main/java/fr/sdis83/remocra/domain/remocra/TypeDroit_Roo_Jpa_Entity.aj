// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeDroit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

privileged aspect TypeDroit_Roo_Jpa_Entity {
    
    declare @type: TypeDroit: @Entity;
    
    @Version
    @Column(name = "version")
    private Integer TypeDroit.version;
    
    public Integer TypeDroit.getVersion() {
        return this.version;
    }
    
    public void TypeDroit.setVersion(Integer version) {
        this.version = version;
    }
    
}
