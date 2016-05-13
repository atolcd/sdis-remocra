package fr.sdis83.remocra.domain.pdi;

import java.math.BigInteger;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class PdiVueCombo {
    private BigInteger id;

    private String libelle;

    public PdiVueCombo(BigInteger id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
}
