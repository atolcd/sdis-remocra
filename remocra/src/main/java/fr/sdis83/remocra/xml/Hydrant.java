package fr.sdis83.remocra.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public abstract class Hydrant {

    private Integer version;

    private String numero;

    private Integer numeroInterne;

    // @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateRecep;

    private Date dateReco;

    private Date dateContr;

    private Date dateVerif;

    private Date dateModification;

    private Date dateGps;

    private String agent1;

    private String agent2;

    private String lieuDit;

    private String voie;

    private String voie2;

    private String complement;

    private Integer anneeFabrication;

    private String dispoTerrestre;

    private String codeDomaine;

    private String codeNature;

    private String codeCommune;

    private String observation;

    private LstAnomalies anomalies;

    private String photo;

    private Coordonnee coordonnee;

    private String courrier;

    private String gestPointEau;

    private boolean verif;

    public Hydrant() {
        //
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getNumeroInterne() {
        return numeroInterne;
    }

    public void setNumeroInterne(Integer numeroInterne) {
        this.numeroInterne = numeroInterne;
    }

    public Date getDateRecep() {
        return dateRecep;
    }

    public void setDateRecep(Date dateRecep) {
        this.dateRecep = dateRecep;
    }

    public Date getDateReco() {
        return dateReco;
    }

    public void setDateReco(Date dateReco) {
        this.dateReco = dateReco;
    }

    public Date getDateContr() {
        return dateContr;
    }

    public void setDateContr(Date dateContr) {
        this.dateContr = dateContr;
    }

    public Date getDateVerif() {
        return dateVerif;
    }

    public void setDateVerif(Date dateVerif) {
        this.dateVerif = dateVerif;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public Date getDateGps() {
        return dateGps;
    }

    public void setDateGps(Date dateGps) {
        this.dateGps = dateGps;
    }

    public String getAgent1() {
        return agent1;
    }

    public void setAgent1(String agent1) {
        this.agent1 = agent1;
    }

    public String getAgent2() {
        return agent2;
    }

    public void setAgent2(String agent2) {
        this.agent2 = agent2;
    }

    public String getLieuDit() {
        return lieuDit;
    }

    public void setLieuDit(String lieuDit) {
        this.lieuDit = lieuDit;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
    }

    public String getVoie2() {
        return voie2;
    }

    public void setVoie2(String voie2) {
        this.voie2 = voie2;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public Integer getAnneeFabrication() {
        return anneeFabrication;
    }

    public void setAnneeFabrication(Integer anneeFabrication) {
        this.anneeFabrication = anneeFabrication;
    }

    @XmlElement(name = "dispo")
    public String getDispoTerrestre() {
        return dispoTerrestre;
    }

    public void setDispoTerrestre(String dispoTerrestre) {
        this.dispoTerrestre = dispoTerrestre;
    }

    @XmlElement(name = "codeCommune", required = true)
    public String getCodeCommune() {
        return codeCommune;
    }

    public void setCodeCommune(String codeCommune) {
        this.codeCommune = codeCommune;
    }

    public String getCodeDomaine() {
        return codeDomaine;
    }

    public void setCodeDomaine(String codeDomaine) {
        this.codeDomaine = codeDomaine;
    }

    public String getCodeNature() {
        return codeNature;
    }

    public void setCodeNature(String codeNature) {
        this.codeNature = codeNature;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LstAnomalies getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(LstAnomalies anomalies) {
        this.anomalies = anomalies;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @XmlElement(name = "coordonnees")
    public Coordonnee getCoordonnee() {
        return coordonnee;
    }

    public void setCoordonnee(Coordonnee coordonnee) {
        this.coordonnee = coordonnee;
    }

    public String getCourrier() {
        return courrier;
    }

    public void setCourrier(String courrier) {
        this.courrier = courrier;
    }

    public String getGestPointEau() {
        return gestPointEau;
    }

    public void setGestPointEau(String gestPointEau) {
        this.gestPointEau = gestPointEau;
    }

    @XmlAttribute
    public boolean isVerif() {
        return verif;
    }

    public void setVerif(boolean verif) {
        this.verif = verif;
    }
}
