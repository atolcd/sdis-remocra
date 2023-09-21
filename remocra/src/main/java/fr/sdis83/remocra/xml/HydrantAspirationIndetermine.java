package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_ASP_I)
public class HydrantAspirationIndetermine extends HydrantPena {
  public HydrantAspirationIndetermine() {
    super.setCodeNature(HydrantPena.CODE_NATURE_ASP_I);
  }
}
