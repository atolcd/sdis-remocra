package fr.sdis83.remocra.web.model.pei;

public class PenaModel {

    boolean capaciteIllimitee;

    boolean ressourceIncertaine;

    String capacite;

    Double debitAppoint;

    String codeMateriau;

    boolean equipeHBE;

    public boolean isCapaciteIllimitee() {
        return capaciteIllimitee;
    }

    public void setCapaciteIllimitee(boolean capaciteIllimitee) {
        this.capaciteIllimitee = capaciteIllimitee;
    }

    public boolean isRessourceIncertaine() {
        return ressourceIncertaine;
    }

    public void setRessourceIncertaine(boolean ressourceIncertaine) {
        this.ressourceIncertaine = ressourceIncertaine;
    }

    public String getCapacite() {
        return capacite;
    }

    public void setCapacite(String capacite) {
        this.capacite = capacite;
    }

    public Double getDebitAppoint() {
        return debitAppoint;
    }

    public void setDebitAppoint(Double debitAppoint) {
        this.debitAppoint = debitAppoint;
    }

    public String getCodeMateriau() {
        return codeMateriau;
    }

    public void setCodeMateriau(String codeMateriau) {
        this.codeMateriau = codeMateriau;
    }

    public boolean isEquipeHBE() {
        return equipeHBE;
    }

    public void setEquipeHBE(boolean equipeHBE) {
        this.equipeHBE = equipeHBE;
    }
}
