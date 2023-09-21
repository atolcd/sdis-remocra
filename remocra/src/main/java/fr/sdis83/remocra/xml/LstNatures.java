package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "natures")
public class LstNatures {

  private ArrayList<Nature> natures;

  public LstNatures() {
    //
  }

  @XmlElement(name = "nature")
  public ArrayList<Nature> getNatures() {
    return natures;
  }

  public void setNatures(ArrayList<Nature> natures) {
    this.natures = natures;
  }
}
