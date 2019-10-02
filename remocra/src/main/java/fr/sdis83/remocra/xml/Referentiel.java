package fr.sdis83.remocra.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "referentiel")
@XmlType(propOrder = { "natures", "anomalies", "communes", "diametres", "domaines", "marques", "materiaux", "positionnements", "volConstates", "naturesDeci", "typeSaisies"})
public class Referentiel {

    private LstAnomalies anomalies;

    private LstCommunes communes;

    private LstDiametres diametres;

    private LstDomaines domaines;

    private LstMarques marques;

    private LstMateriaux materiaux;

    private LstNatures natures;

    private LstPositionnements positionnements;

    private LstVolConstates volConstates;

    private LstNaturesDeci naturesDeci;

    private LstSaisies typeSaisies;

    public Referentiel() {
        //
    }

    public LstAnomalies getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(LstAnomalies anomalies) {
        this.anomalies = anomalies;
    }

    public LstCommunes getCommunes() {
        return communes;
    }

    public void setCommunes(LstCommunes communes) {
        this.communes = communes;
    }

    public LstDiametres getDiametres() {
        return diametres;
    }

    public void setDiametres(LstDiametres diametres) {
        this.diametres = diametres;
    }

    public LstDomaines getDomaines() {
        return domaines;
    }

    public void setDomaines(LstDomaines domaines) {
        this.domaines = domaines;
    }

    public LstMarques getMarques() {
        return marques;
    }

    public void setMarques(LstMarques marques) {
        this.marques = marques;
    }

    public LstMateriaux getMateriaux() {
        return materiaux;
    }

    public void setMateriaux(LstMateriaux materiaux) {
        this.materiaux = materiaux;
    }

    public LstNatures getNatures() {
        return natures;
    }

    public void setNatures(LstNatures natures) {
        this.natures = natures;
    }

    public LstPositionnements getPositionnements() {
        return positionnements;
    }

    public void setPositionnements(LstPositionnements positionnements) {
        this.positionnements = positionnements;
    }

    public LstVolConstates getVolConstates() {
        return volConstates;
    }

    public void setVolConstates(LstVolConstates volConstates) {
        this.volConstates = volConstates;
    }

    public LstNaturesDeci getNaturesDeci() {
        return naturesDeci;
    }

    public void setNaturesDeci(LstNaturesDeci naturesDeci) {
        this.naturesDeci = naturesDeci;
    }

    public LstSaisies getTypeSaisies() {
        return typeSaisies;
    }

    public void setTypeSaisies(LstSaisies typeSaisies) {
        this.typeSaisies = typeSaisies;
    }
}
