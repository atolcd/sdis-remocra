package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tournees", namespace = "http://www.w3.org/2001/XMLSchema-instance")
public class LstTournees {

    private ArrayList<Tournee> tournees = new ArrayList<Tournee>();;

    public LstTournees() {
        //
    }

    @XmlElement(name = "tournee")
    public ArrayList<Tournee> getTournees() {
        return tournees;
    }

    public void setTournees(ArrayList<Tournee> tournees) {
        this.tournees = tournees;
    }
}
