// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.Metadonnee;
import fr.sdis83.remocra.domain.remocra.Thematique;

privileged aspect Metadonnee_Roo_JavaBean {
    
    public Long Metadonnee.getId() {
        return this.id;
    }
    
    public void Metadonnee.setId(Long id) {
        this.id = id;
    }
    
    public String Metadonnee.getTitre() {
        return this.titre;
    }
    
    public void Metadonnee.setTitre(String titre) {
        this.titre = titre;
    }
    
    public String Metadonnee.getResume() {
        return this.resume;
    }
    
    public void Metadonnee.setResume(String resume) {
        this.resume = resume;
    }
    
    public String Metadonnee.getUrlVignette() {
        return this.urlVignette;
    }
    
    public void Metadonnee.setUrlVignette(String urlVignette) {
        this.urlVignette = urlVignette;
    }
    
    public String Metadonnee.getUrlFiche() {
        return this.urlFiche;
    }
    
    public void Metadonnee.setUrlFiche(String urlFiche) {
        this.urlFiche = urlFiche;
    }
    
    public Thematique Metadonnee.getThematique() {
        return this.thematique;
    }
    
    public void Metadonnee.setThematique(Thematique thematique) {
        this.thematique = thematique;
    }

    public void Metadonnee.setCodeExport(String codeExport) {
		this.codeExport = codeExport;
    }

    public String Metadonnee.getCodeExport() {
		return this.codeExport;
    }

}
