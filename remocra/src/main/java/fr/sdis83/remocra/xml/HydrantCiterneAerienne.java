package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CI_AE)
public class HydrantCiterneAerienne extends HydrantCiterneEnterre {
    public HydrantCiterneAerienne() {
        super.setCodeNature(HydrantPena.CODE_NATURE_CI_AE);
    }
}
