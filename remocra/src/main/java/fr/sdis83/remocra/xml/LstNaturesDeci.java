package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "naturesDeci")
public class LstNaturesDeci {

  private ArrayList<NatureDeci> naturesDeci;

  public LstNaturesDeci() {
    //
  }

  @XmlElement(name = "natureDeci")
  public ArrayList<NatureDeci> getNaturesDeci() {
    return naturesDeci;
  }

  public void setNaturesDeci(ArrayList<NatureDeci> naturesDeci) {
    this.naturesDeci = naturesDeci;
  }
}
