package fr.sdis83.remocra.web.model.authn;

import javax.annotation.Nullable;

public class ParamConfModel {
    public String cle;

    @Nullable
    public String description;

    public String valeur;

    public Integer version;
    public String nomgroupe;

    public String getCle() {
        return cle;
    }

    public String getDescription() {
        return description;
    }

    public String getValeur() {
        return valeur;
    }

    public Integer getVersion() {
        return version;
    }

    public String getNomgroupe() {
        return nomgroupe;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setNomGroupe(String nomGroupe) {
        this.nomgroupe = nomGroupe;
    }

}
