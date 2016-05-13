package fr.sdis83.remocra.holder;

/**
 * Created by cva on 07/02/14.
 */
public class NatureHolder extends ReferentielHolder {

    String typeNature;

    public NatureHolder(Long id, String code, String libelle, String typeNature) {
        super(id, code, libelle);
        this.typeNature = typeNature;
    }

    public String getTypeNature() {
        return typeNature;
    }

    public void setTypeNature(String typeNature) {
        this.typeNature = typeNature;
    }
}
