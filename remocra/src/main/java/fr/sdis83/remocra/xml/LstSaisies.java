package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "saisies")
public class LstSaisies {

    private ArrayList<Saisie> saisies;

    public LstSaisies() {
        //
    }

    @XmlElement(name = "saisie")
    public ArrayList<Saisie> getSaisies() {
        return saisies;
    }

    public void setSaisies(ArrayList<Saisie> saisies) {
        this.saisies = saisies;
    }
}
