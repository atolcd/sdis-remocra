// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.CourrierModele;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

privileged aspect CourrierModele_Roo_DbManaged {
    
    @Column(name = "code")
    private String CourrierModele.code;
    
    @Column(name = "libelle")
    @NotNull
    private String CourrierModele.libelle;
    
    @Column(name = "description")
    private String CourrierModele.description;
    
    @Column(name = "modele_ott")
    @NotNull
    private String CourrierModele.modeleOtt;
    
    @Column(name = "source_xml")
    @NotNull
    private String CourrierModele.sourceXml;
    
    public String CourrierModele.getCode() {
        return code;
    }
    
    public void CourrierModele.setCode(String code) {
        this.code = code;
    }
    
    public String CourrierModele.getLibelle() {
        return libelle;
    }
    
    public void CourrierModele.setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String CourrierModele.getDescription() {
        return description;
    }
    
    public void CourrierModele.setDescription(String description) {
        this.description = description;
    }
    
    public String CourrierModele.getModeleOtt() {
        return modeleOtt;
    }
    
    public void CourrierModele.setModeleOtt(String modeleOtt) {
        this.modeleOtt = modeleOtt;
    }
    
    public String CourrierModele.getSourceXml() {
        return sourceXml;
    }
    
    public void CourrierModele.setSourceXml(String sourceXml) {
        this.sourceXml = sourceXml;
    }
    
}
