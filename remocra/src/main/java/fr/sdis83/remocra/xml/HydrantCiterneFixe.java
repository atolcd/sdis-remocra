package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CI_FIXE)
public class HydrantCiterneFixe extends HydrantCiterneEnterre {

    private String codePositionnement;

    public HydrantCiterneFixe() {
        super.setCodeNature(HydrantPena.CODE_NATURE_CI_FIXE);
    }

    public String getCodePositionnement() {
        return codePositionnement;
    }

    public void setCodePositionnement(String codePositionnement) {
        this.codePositionnement = codePositionnement;
    }

}
