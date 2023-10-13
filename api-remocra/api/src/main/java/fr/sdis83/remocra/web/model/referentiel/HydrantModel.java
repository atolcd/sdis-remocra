package fr.sdis83.remocra.web.model.referentiel;

public class HydrantModel {

  Long idRemocra;
  Long idNature;
  Long idNatureDeci;
  String dispoHbe;
  String dispoTerrestre;
  Double x;
  Double y;
  Double lon;
  Double lat;
  String numero;
  String code;
  String adresseComplete;
  String nomCommune;
  String codeCommune;
  String complement;
  String voie;
  String voie2;
  String suffixeVoie;
  String numeroVoie;
  String lieuDit;
  String observation;
  Long idRemocraGestionnaire;

  public Long getIdRemocra() {
    return idRemocra;
  }

  public void setIdRemocra(Long idRemocra) {
    this.idRemocra = idRemocra;
  }

  public Long getIdNature() {
    return idNature;
  }

  public void setIdNature(Long idNature) {
    this.idNature = idNature;
  }

  public Long getIdNatureDeci() {
    return idNatureDeci;
  }

  public void setIdNatureDeci(Long idNatureDeci) {
    this.idNatureDeci = idNatureDeci;
  }

  public String getDispoHbe() {
    return dispoHbe;
  }

  public void setDispoHbe(String dispoHbe) {
    this.dispoHbe = dispoHbe;
  }

  public String getDispoTerrestre() {
    return dispoTerrestre;
  }

  public void setDispoTerrestre(String dispoTerrestre) {
    this.dispoTerrestre = dispoTerrestre;
  }

  public Double getX() {
    return x;
  }

  public void setX(Double x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }

  public Double getLon() {
    return lon;
  }

  public void setLon(Double lon) {
    this.lon = lon;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public String getAdresseComplete() {
    return adresseComplete;
  }

  public void setAdresseComplete(String adresseComplete) {
    this.adresseComplete = adresseComplete;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getObservation() {
    return observation;
  }

  public void setObservation(String observation) {
    this.observation = observation;
  }

  public String getNomCommune() {
    return nomCommune;
  }

  public String getCodeCommune() {
    return codeCommune;
  }

  public String getComplement() {
    return complement;
  }

  public String getVoie() {
    return voie;
  }

  public String getVoie2() {
    return voie2;
  }

  public String getSuffixeVoie() {
    return suffixeVoie;
  }

  public String getLieuDit() {
    return lieuDit;
  }

  public String getNumeroVoie() {
    return numeroVoie;
  }

  public Long getIdRemocraGestionnaire() {
    return idRemocraGestionnaire;
  }

  public void setIdRemocraGestionnaire(Long idRemocraGestionnaire) {
    this.idRemocraGestionnaire = idRemocraGestionnaire;
  }
}
