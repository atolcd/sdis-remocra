package fr.sdis83.remocra.domain.remocra;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class RemocraVueCombo {
    private String id;

    private String libelle;

    public RemocraVueCombo(String id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
}
