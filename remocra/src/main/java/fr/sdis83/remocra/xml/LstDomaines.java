package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "domaines")
public class LstDomaines {

  private ArrayList<Domaine> domaines;

  public LstDomaines() {
    //
  }

  @XmlElement(name = "domaine")
  public ArrayList<Domaine> getDomaines() {
    return domaines;
  }

  public void setDomaines(ArrayList<Domaine> domaines) {
    this.domaines = domaines;
  }
}
