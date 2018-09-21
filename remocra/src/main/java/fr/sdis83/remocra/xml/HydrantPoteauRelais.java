package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_PR)
public class HydrantPoteauRelais extends HydrantPena {
    public HydrantPoteauRelais() {
        super.setCodeNature(HydrantPena.CODE_NATURE_PR);
    }
}
