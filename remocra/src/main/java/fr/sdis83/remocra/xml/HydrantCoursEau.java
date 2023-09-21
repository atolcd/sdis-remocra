package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CE)
public class HydrantCoursEau extends HydrantPena {
  public HydrantCoursEau() {
    super.setCodeNature(HydrantPena.CODE_NATURE_CE);
  }
}
