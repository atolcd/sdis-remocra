package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "marques")
public class LstMarques {

    private ArrayList<Marque> marques;

    public LstMarques() {
        //
    }

    @XmlElement(name = "marque")
    public ArrayList<Marque> getMarques() {
        return marques;
    }

    public void setMarques(ArrayList<Marque> marques) {
        this.marques = marques;
    }
}
