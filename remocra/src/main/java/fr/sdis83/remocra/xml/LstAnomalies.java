package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "anomalies")
public class LstAnomalies {

    private ArrayList<Anomalie> anomalies;

    public LstAnomalies() {
        //
    }

    @XmlElement(name = "anomalie")
    public ArrayList<Anomalie> getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(ArrayList<Anomalie> anomalies) {
        this.anomalies = anomalies;
    }
}
