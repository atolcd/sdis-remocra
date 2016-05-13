package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "volConstates")
public class LstVolConstates {

    private ArrayList<VolConstate> volConstates;

    public LstVolConstates() {
        //
    }

    @XmlElement(name="volConstate")
    public ArrayList<VolConstate> getVolConstates() {
        return volConstates;
    }

    public void setVolConstates(ArrayList<VolConstate> volConstates) {
        this.volConstates = volConstates;
    }
}
