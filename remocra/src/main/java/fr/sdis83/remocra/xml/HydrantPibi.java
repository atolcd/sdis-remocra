package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ HydrantPi.class, HydrantBi.class, HydrantPa.class })
public abstract class HydrantPibi extends Hydrant {

    public static final String CODE_NATURE_PI = "PI";
    public static final String CODE_NATURE_BI = "BI";
    public static final String CODE_NATURE_PA = "PA";

    public HydrantPibi() {
        //
    }

    private Integer debit;

    private Integer debitMax;

    private Double pression;

    private Double pressionDyn;
    
    private Double pressionDynDeb;

    private String gestReseau;

    private String numeroSCP;

    private Boolean renversable;

    private String codeMarque;

    private String codeModele;

    private String codeDiametre;

    private String codePena;

    private String jumele;

    private Boolean grosDebit;

    private Boolean debitRenforce;

    public Integer getDebit() {
        return debit;
    }

    public void setDebit(Integer debit) {
        this.debit = debit;
    }

    public Integer getDebitMax() {
        return debitMax;
    }

    public void setDebitMax(Integer debitMax) {
        this.debitMax = debitMax;
    }

    public Double getPression() {
        return pression;
    }

    public void setPression(Double pression) {
        this.pression = pression;
    }

    public Double getPressionDyn() {
        return pressionDyn;
    }

    public void setPressionDyn(Double pressionDyn) {
        this.pressionDyn = pressionDyn;
    }

    public String getGestReseau() {
        return gestReseau;
    }

    public void setGestReseau(String gestReseau) {
        this.gestReseau = gestReseau;
    }

    public String getNumeroSCP() {
        return numeroSCP;
    }

    public void setNumeroSCP(String numeroSCP) {
        this.numeroSCP = numeroSCP;
    }

    // Phase de transition
    @XmlElement(name = "choc")
    public Boolean getRenversable() {
        return renversable;
    }

    public void setRenversable(Boolean renversable) {
        this.renversable = renversable;
    }

    public String getCodeMarque() {
        return codeMarque;
    }

    public void setCodeMarque(String codeMarque) {
        this.codeMarque = codeMarque;
    }

    public String getCodeModele() {
        return codeModele;
    }

    public void setCodeModele(String codeModele) {
        this.codeModele = codeModele;
    }

    public String getCodeDiametre() {
        return codeDiametre;
    }

    public void setCodeDiametre(String codeDiametre) {
        this.codeDiametre = codeDiametre;
    }

    public String getCodePena() {
        return codePena;
    }

    public void setCodePena(String codePena) {
        this.codePena = codePena;
    }
    
    public Double getPressionDynDeb() {
        return pressionDynDeb;
    }

    public void setPressionDynDeb(Double pressionDynDeb) {
        this.pressionDynDeb = pressionDynDeb;
    }

    public Boolean getGrosDebit() {
        return grosDebit;
    }

    public void setGrosDebit(Boolean grosDebit) {
        this.grosDebit = grosDebit;
    }

    public String getJumele() {
        return jumele;
    }

    public void setJumele(String jumele) {
        this.jumele = jumele;
    }

    public Boolean getDebitRenforce() {
        return debitRenforce;
    }

    public void setDebitRenforce(Boolean debitRenforce) {
        this.debitRenforce = debitRenforce;
    }
}
