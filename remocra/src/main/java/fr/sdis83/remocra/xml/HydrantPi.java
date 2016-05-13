package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPibi.CODE_NATURE_PI)
public class HydrantPi extends HydrantPibi {
    public HydrantPi() {
        super.setCodeNature(HydrantPibi.CODE_NATURE_PI);
    }
}
