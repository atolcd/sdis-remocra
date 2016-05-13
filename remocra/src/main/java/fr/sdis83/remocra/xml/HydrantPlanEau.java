package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_PE)
public class HydrantPlanEau extends HydrantPena {
    public HydrantPlanEau() {
        super.setCodeNature(HydrantPena.CODE_NATURE_PE);
    }
}
