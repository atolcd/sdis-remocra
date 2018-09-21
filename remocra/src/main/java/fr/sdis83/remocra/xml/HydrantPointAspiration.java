package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_PA_I)
public class HydrantPointAspiration extends HydrantPena {
    public HydrantPointAspiration() {
        super.setCodeNature(HydrantPena.CODE_NATURE_PA_I);
    }
}
