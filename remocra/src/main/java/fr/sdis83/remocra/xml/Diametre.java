package fr.sdis83.remocra.xml;

public class Diametre {

    private String code;

    private String libelle;

    private LstNatures natures;

    public Diametre() {
        //
    }

    public Diametre(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public LstNatures getNatures() {
        return natures;
    }

    public void setNatures(LstNatures natures) {
        this.natures = natures;
    }

    @Override
    public String toString() {
        return this.code + " " + this.libelle;
    }

}
