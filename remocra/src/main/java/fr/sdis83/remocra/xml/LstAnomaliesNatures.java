package fr.sdis83.remocra.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "natures")
public class LstAnomaliesNatures {

    private ArrayList<AnomalieNature> anomaliesNatures;

    public LstAnomaliesNatures() {
        //
    }

    @XmlElement(name = "nature")
    public ArrayList<AnomalieNature> getAnomaliesNatures() {
        return anomaliesNatures;
    }

    public void setAnomaliesNatures(ArrayList<AnomalieNature> anomaliesNatures) {
        this.anomaliesNatures = anomaliesNatures;
    }
}
