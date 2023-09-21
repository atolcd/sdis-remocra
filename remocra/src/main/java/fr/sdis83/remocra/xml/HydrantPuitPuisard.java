package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_PUI)
public class HydrantPuitPuisard extends HydrantPena {
  public HydrantPuitPuisard() {
    super.setCodeNature(HydrantPena.CODE_NATURE_PUI);
  }
}
