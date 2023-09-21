package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPibi.CODE_NATURE_PA)
public class HydrantPa extends HydrantPibi {
  public HydrantPa() {
    super.setCodeNature(HydrantPibi.CODE_NATURE_PA);
  }
}
