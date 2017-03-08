package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_RI)
public class HydrantReserveIncendie extends HydrantPena{
    public HydrantReserveIncendie(){
        super.setCodeNature(HydrantPena.CODE_NATURE_RI);
    }
}
