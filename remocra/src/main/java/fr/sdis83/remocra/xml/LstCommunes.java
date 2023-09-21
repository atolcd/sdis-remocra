package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "communes")
public class LstCommunes {

  private ArrayList<Commune> communes;

  public LstCommunes() {
    //
  }

  @XmlElement(name = "commune")
  public ArrayList<Commune> getCommunes() {
    return communes;
  }

  public void setCommunes(ArrayList<Commune> communes) {
    this.communes = communes;
  }
}
