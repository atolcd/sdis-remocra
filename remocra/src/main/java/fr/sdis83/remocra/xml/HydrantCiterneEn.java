package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_CI_EN)
public class HydrantCiterneEn extends HydrantCiterneEnterre {

  public HydrantCiterneEn() {
    super.setCodeNature(HydrantPena.CODE_NATURE_CI_EN);
  }
}
