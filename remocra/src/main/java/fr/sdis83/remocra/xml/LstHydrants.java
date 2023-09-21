package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hydrants", namespace = "http://www.w3.org/2001/XMLSchema-instance")
public class LstHydrants {

  private ArrayList<HydrantPibi> hydrantsPibi = new ArrayList<HydrantPibi>();

  private ArrayList<HydrantPena> hydrantsPena = new ArrayList<HydrantPena>();

  public LstHydrants() {
    //
  }

  @XmlElement(name = "hydrantPibi")
  public ArrayList<HydrantPibi> getHydrantsPibi() {
    return hydrantsPibi;
  }

  public void setHydrantsPibi(ArrayList<HydrantPibi> hydrantsPibi) {
    this.hydrantsPibi = hydrantsPibi;
  }

  @XmlElement(name = "hydrantPena")
  public ArrayList<HydrantPena> getHydrantsPena() {
    return hydrantsPena;
  }

  public void setHydrantsPena(ArrayList<HydrantPena> hydrantsPena) {
    this.hydrantsPena = hydrantsPena;
  }
}
