package fr.sdis83.remocra.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "diametres")
public class LstDiametres {

  private ArrayList<Diametre> diametres;

  public LstDiametres() {
    //
  }

  @XmlElement(name = "diametre")
  public ArrayList<Diametre> getDiametres() {
    return diametres;
  }

  public void setDiametres(ArrayList<Diametre> diametres) {
    this.diametres = diametres;
  }
}
