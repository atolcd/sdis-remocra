package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "modeles")
public class LstModeles {

    private ArrayList<Modele> modeles;

    public LstModeles() {
        //
    }

    @XmlElement(name = "modele")
    public ArrayList<Modele> getModeles() {
        return modeles;
    }

    public void setModeles(ArrayList<Modele> modeles) {
        this.modeles = modeles;
    }
}
