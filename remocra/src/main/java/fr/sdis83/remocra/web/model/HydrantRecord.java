package fr.sdis83.remocra.web.model;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Organisme;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNatureDeci;

import java.util.Date;
import java.util.List;

public class HydrantRecord {


    public static enum Disponibilite {
        DISPO, INDISPO, NON_CONFORME
    }
    private Long id;

    private String code;

    private CommuneRecord commune;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private Date dateReco;
    private Date dateContr;
    private Date dateRecep;

    private String jsonGeometrie;

    private String wktGeometrie;

    public String getJsonGeometrie() {
        return jsonGeometrie;
    }

    public void setJsonGeometrie(String jsonGeometrie) {
        this.jsonGeometrie = jsonGeometrie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Integer numeroInterne;
    private List<Tournee> tournees;

    private String adresse;
    private String numero;
    private String nomTournee;

    private Integer indispoTemp;

    public Disponibilite getDispoTerrestre() {
        return dispoTerrestre;
    }

    public void setDispoTerrestre(Disponibilite dispoTerrestre) {
        this.dispoTerrestre = dispoTerrestre;
    }

    private String nomCommune;

    private Disponibilite dispoTerrestre;

    private Disponibilite dispoHbe;

    public Disponibilite getDispoHbe() {
        return dispoHbe;
    }

    public void setDispoHbe(Disponibilite dispoHbe) {
        this.dispoHbe = dispoHbe;
    }

    public Organisme getOrganisme() {
        return organisme;
    }

    public void setOrganisme(Organisme organisme) {
        this.organisme = organisme;
    }

    private String nomNatureDeci;

    private Organisme organisme;

    private Geometry geometrie;

    public String getNatureNom() {
        return natureNom;
    }

    public void setNatureNom(String natureNom) {
        this.natureNom = natureNom;
    }


    private String natureNom;

    public String getNomNatureDeci() {
        return nomNatureDeci;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setNomNatureDeci(String nomNatureDeci) {
        this.nomNatureDeci = nomNatureDeci;
    }

    private TypeHydrantNatureDeci natureDeci;



    private TypeHydrantNature nature;

    public String getNomCommune() {
        return nomCommune;
    }

    public void setNomCommune(String nomCommune) {
        this.nomCommune = nomCommune;
    }


    public CommuneRecord getCommune() {
        return commune;
    }

    public void setCommune(CommuneRecord commune) {
        this.commune = commune;
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

    public Date getDateRecep() {
        return dateRecep;
    }

    public void setDateRecep(Date dateRecep) {
        this.dateRecep = dateRecep;
    }

    public Integer getNumeroInterne() {
        return numeroInterne;
    }

    public void setNumeroInterne(Integer numeroInterne) {
        this.numeroInterne = numeroInterne;
    }

    public List<Tournee> getTournees() {
        return tournees;
    }

    public void setTournees(List<Tournee> tournees) {
        this.tournees = tournees;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomTournee() {
        return nomTournee;
    }

    public void setNomTournee(String nomTournee) {
        this.nomTournee = nomTournee;
    }

    public TypeHydrantNatureDeci getNatureDeci() {
        return natureDeci;
    }

    public void setNatureDeci(TypeHydrantNatureDeci natureDeci) {
        this.natureDeci = natureDeci;
    }

    public TypeHydrantNature getNature() {
        return nature;
    }

    public void setNature(TypeHydrantNature nature) {
        this.nature = nature;
    }

    public Geometry getGeometrie() {
        return geometrie;
    }

    public void setGeometrie(Geometry geometrie) {
        this.geometrie = geometrie;
    }

    public String getWktGeometrie() {
        return wktGeometrie;
    }

    public void setWktGeometrie(String wktGeometrie) {
        this.wktGeometrie = wktGeometrie;
    }

    public Integer getIndispoTemp() {
        return indispoTemp;
    }

    public void setIndispoTemp(Integer indispoTemp) {
        this.indispoTemp = indispoTemp;
    }
}
