package fr.sdis83.remocra.holder;

/**
 * Created by cva on 07/02/14.
 */
public class ReferentielHolder {

    Long id;
    String code;
    String libelle;

    public ReferentielHolder(Long id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
