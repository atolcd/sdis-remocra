package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_PU)
public class HydrantPuisard extends HydrantPena {
    public HydrantPuisard() {
        super.setCodeNature(HydrantPena.CODE_NATURE_PU);
    }
}
