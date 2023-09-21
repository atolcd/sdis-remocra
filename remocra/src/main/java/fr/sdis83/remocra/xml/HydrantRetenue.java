package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = HydrantPena.CODE_NATURE_RE)
public class HydrantRetenue extends HydrantPena {
  public HydrantRetenue() {
    super.setCodeNature(HydrantPena.CODE_NATURE_RE);
  }
}
