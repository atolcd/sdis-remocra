package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CHE)
public class HydrantChateauEau extends HydrantPena {
    public HydrantChateauEau() {
        super.setCodeNature(HydrantPena.CODE_NATURE_CHE);
    }
}
