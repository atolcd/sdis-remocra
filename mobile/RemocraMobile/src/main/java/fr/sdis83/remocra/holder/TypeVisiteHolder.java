package fr.sdis83.remocra.holder;

/**
 * Created by yaz on 30/10/19.
 */
public class TypeVisiteHolder extends ReferentielHolder {


    public TypeVisiteHolder(Long id, String code, String libelle) {
        super(id, code, libelle);
    }

    @Override
    public String toString(){
        return this.getLibelle();
    }


}
