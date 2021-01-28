package fr.sdis83.remocra.web.model.pei;

public class PeiModel {

    String insee;

    String idSdis;

    String idGestion;

    String nomGest;

    String refTerr;

    String typePei;

    String typeRd;

    String diamPei;

    String diamCana;

    String sourcePei;

    String statut;

    String nomEtab;

    String situation;

    Integer pressDyn;

    Integer pressStat;

    Integer debit;

    Integer volume;

    Boolean disponible;

    String dateDispo;

    String dateMes;

    String dateMaj;

    String dateCt;

    String dateRo;

    Double x;

    Double y;

    Double lon;

    Double lat;

    String prec;

    public String getInsee() {
        return insee;
    }

    public void setInsee(String insee) {
        this.insee = insee;
    }

    public String getIdSdis() {
        return idSdis;
    }

    public void setIdSdis(String idSdis) {
        this.idSdis = idSdis;
    }

    public String getIdGestion() {
        return idGestion;
    }

    public void setIdGestion(String idGestion) {
        this.idGestion = idGestion;
    }

    public String getNomGest() {
        return nomGest;
    }

    public void setNomGest(String nomGest) {
        this.nomGest = nomGest;
    }

    public String getRefTerr() {
        return refTerr;
    }

    public void setRefTerr(String refTerr) {
        this.refTerr = refTerr;
    }

    public String getTypePei() {
        return typePei;
    }

    public void setTypePei(String typePei) {
        this.typePei = typePei;
    }

    public String getTypeRd() {
        return typeRd;
    }

    public void setTypeRd(String typeRd) {
        this.typeRd = typeRd;
    }

    public String getDiamPei() {
        return diamPei;
    }

    public void setDiamPei(String diamPei) {
        this.diamPei = diamPei;
    }

    public String getDiamCana() {
        return diamCana;
    }

    public void setDiamCana(String diamCana) {
        this.diamCana = diamCana;
    }

    public String getSourcePei() {
        return sourcePei;
    }

    public void setSourcePei(String sourcePei) {
        this.sourcePei = sourcePei;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNomEtab() {
        return nomEtab;
    }

    public void setNomEtab(String nomEtab) {
        this.nomEtab = nomEtab;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public Integer getPressDyn() {
        return pressDyn;
    }

    public void setPressDyn(Integer pressDyn) {
        this.pressDyn = pressDyn;
    }

    public Integer getPressStat() {
        return pressStat;
    }

    public void setPressStat(Integer pressStat) {
        this.pressStat = pressStat;
    }

    public Integer getDebit() {
        return debit;
    }

    public void setDebit(Integer debit) {
        this.debit = debit;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getDateDispo() {
        return dateDispo;
    }

    public void setDateDispo(String dateDispo) {
        this.dateDispo = dateDispo.substring(0,16).replace('T', ' ');
    }

    public String getDateMes() {
        return dateMes;
    }

    public void setDateMes(String dateMes) {
        this.dateMes = dateMes.substring(0,16).replace('T', ' ');
    }

    public String getDateMaj() {
        return dateMaj;
    }

    public void setDateMaj(String dateMaj) {
        this.dateMaj = dateMaj.substring(0,16).replace('T', ' ');
    }

    public String getDateCt() {
        return dateCt;
    }

    public void setDateCt(String dateCt) {
        this.dateCt = dateCt.substring(0,16).replace('T', ' ');
    }

    public String getDateRo() {
        return dateRo;
    }

    public void setDateRo(String dateRo) {
        this.dateRo = dateRo.substring(0,16).replace('T', ' ');
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

    public String getPrec() {
        return prec;
    }

    public void setPrec(String prec) {
        this.prec = prec;
    }
}