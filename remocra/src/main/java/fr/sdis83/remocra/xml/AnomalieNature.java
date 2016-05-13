package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "codeNature", "valeur", "valeurAdmin", "saisies" })
public class AnomalieNature {

    private String codeNature;

    private Integer valeur;

    private Integer valeurAdmin;

    private LstSaisies saisies;

    public AnomalieNature() {
        //
    }

    public LstSaisies getSaisies() {
        return saisies;
    }

    public void setSaisies(LstSaisies saisies) {
        this.saisies = saisies;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    public Integer getValeurAdmin() {
        return valeurAdmin;
    }

    public void setValeurAdmin(Integer valeurAdmin) {
        this.valeurAdmin = valeurAdmin;
    }

    public String getCodeNature() {
        return codeNature;
    }

    public void setCodeNature(String codeNature) {
        this.codeNature = codeNature;
    }
}
