// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.DepotDocument;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Utilisateur;

privileged aspect DepotDocument_Roo_JavaBean {
    
    public Long DepotDocument.getId() {
        return this.id;
    }
    
    public void DepotDocument.setId(Long id) {
        this.id = id;
    }
    
    public Document DepotDocument.getDocument() {
        return this.document;
    }
    
    public void DepotDocument.setDocument(Document document) {
        this.document = document;
    }
    
    public Utilisateur DepotDocument.getUtilisateur() {
        return this.utilisateur;
    }
    
    public void DepotDocument.setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
}
