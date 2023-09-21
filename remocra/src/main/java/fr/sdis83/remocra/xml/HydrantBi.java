package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPibi.CODE_NATURE_BI)
public class HydrantBi extends HydrantPibi {
  public HydrantBi() {
    super.setCodeNature(HydrantPibi.CODE_NATURE_BI);
  }
}
