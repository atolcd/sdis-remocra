package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "positionnements")
public class LstPositionnements {

    private ArrayList<Positionnement> positionnements;

    public LstPositionnements() {
        //
    }

    @XmlElement(name = "positionnement")
    public ArrayList<Positionnement> getPositionnements() {
        return positionnements;
    }

    public void setPositionnements(ArrayList<Positionnement> positionnements) {
        this.positionnements = positionnements;
    }
}
