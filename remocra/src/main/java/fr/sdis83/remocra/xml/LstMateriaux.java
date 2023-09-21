package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "materiaux")
public class LstMateriaux {

  private ArrayList<Materiau> materiaux;

  public LstMateriaux() {
    //
  }

  @XmlElement(name = "materiau")
  public ArrayList<Materiau> getMateriaux() {
    return materiaux;
  }

  public void setMateriaux(ArrayList<Materiau> materiaux) {
    this.materiaux = materiaux;
  }
}
