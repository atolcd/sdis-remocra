/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tracabilite.tables.pojos;


import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Hydrant implements Serializable {

    private static final long serialVersionUID = -997735077;

    private Integer  id;
    private Long     numTransac;
    private String   nomOperation;
    private Instant  dateOperation;
    private Long     idHydrant;
    private String   numero;
    private Object   geometrie;
    private String   insee;
    private String   commune;
    private String   lieuDit;
    private String   voie;
    private String   carrefour;
    private String   complement;
    private String   agent1;
    private String   agent2;
    private Instant  dateRecep;
    private Instant  dateReco;
    private Instant  dateContr;
    private Instant  dateVerif;
    private String   dispoTerrestre;
    private String   dispoHbe;
    private String   nature;
    private String   typeHydrant;
    private String[] anomalies;
    private String   observation;
    private String   auteurModification;
    private Boolean  hbe;
    private String   positionnement;
    private String   materiau;
    private String   volConstate;
    private String   diametre;
    private Integer  debit;
    private Integer  debitMax;
    private Double   pression;
    private Double   pressionDyn;
    private String   marque;
    private String   modele;
    private Double   pressionDynDeb;
    private String   domaine;
    private String   capacite;
    private String   natureDeci;
    private Integer  numeroVoie;
    private String   suffixeVoie;
    private String   niveau;
    private String   gestionnaire;
    private String   site;
    private String   autoriteDeci;
    private String   jumele;
    private Boolean  dispositifInviolabilite;
    private String   reservoir;
    private String   serviceEaux;
    private Boolean  debitRenforce;
    private String   typeReseauCanalisation;
    private String   typeReseauAlimentation;
    private Integer  diametreCanalisation;
    private Boolean  surpresse;
    private Boolean  additive;
    private Boolean  illimitee;
    private Boolean  incertaine;
    private Boolean  enFace;
    private String   spDeci;
    private Long     utilisateurModification;
    private Long     organisme;
    private String   auteurModificationFlag;

    public Hydrant() {}

    public Hydrant(Hydrant value) {
        this.id = value.id;
        this.numTransac = value.numTransac;
        this.nomOperation = value.nomOperation;
        this.dateOperation = value.dateOperation;
        this.idHydrant = value.idHydrant;
        this.numero = value.numero;
        this.geometrie = value.geometrie;
        this.insee = value.insee;
        this.commune = value.commune;
        this.lieuDit = value.lieuDit;
        this.voie = value.voie;
        this.carrefour = value.carrefour;
        this.complement = value.complement;
        this.agent1 = value.agent1;
        this.agent2 = value.agent2;
        this.dateRecep = value.dateRecep;
        this.dateReco = value.dateReco;
        this.dateContr = value.dateContr;
        this.dateVerif = value.dateVerif;
        this.dispoTerrestre = value.dispoTerrestre;
        this.dispoHbe = value.dispoHbe;
        this.nature = value.nature;
        this.typeHydrant = value.typeHydrant;
        this.anomalies = value.anomalies;
        this.observation = value.observation;
        this.auteurModification = value.auteurModification;
        this.hbe = value.hbe;
        this.positionnement = value.positionnement;
        this.materiau = value.materiau;
        this.volConstate = value.volConstate;
        this.diametre = value.diametre;
        this.debit = value.debit;
        this.debitMax = value.debitMax;
        this.pression = value.pression;
        this.pressionDyn = value.pressionDyn;
        this.marque = value.marque;
        this.modele = value.modele;
        this.pressionDynDeb = value.pressionDynDeb;
        this.domaine = value.domaine;
        this.capacite = value.capacite;
        this.natureDeci = value.natureDeci;
        this.numeroVoie = value.numeroVoie;
        this.suffixeVoie = value.suffixeVoie;
        this.niveau = value.niveau;
        this.gestionnaire = value.gestionnaire;
        this.site = value.site;
        this.autoriteDeci = value.autoriteDeci;
        this.jumele = value.jumele;
        this.dispositifInviolabilite = value.dispositifInviolabilite;
        this.reservoir = value.reservoir;
        this.serviceEaux = value.serviceEaux;
        this.debitRenforce = value.debitRenforce;
        this.typeReseauCanalisation = value.typeReseauCanalisation;
        this.typeReseauAlimentation = value.typeReseauAlimentation;
        this.diametreCanalisation = value.diametreCanalisation;
        this.surpresse = value.surpresse;
        this.additive = value.additive;
        this.illimitee = value.illimitee;
        this.incertaine = value.incertaine;
        this.enFace = value.enFace;
        this.spDeci = value.spDeci;
        this.utilisateurModification = value.utilisateurModification;
        this.organisme = value.organisme;
        this.auteurModificationFlag = value.auteurModificationFlag;
    }

    public Hydrant(
        Integer  id,
        Long     numTransac,
        String   nomOperation,
        Instant  dateOperation,
        Long     idHydrant,
        String   numero,
        Object   geometrie,
        String   insee,
        String   commune,
        String   lieuDit,
        String   voie,
        String   carrefour,
        String   complement,
        String   agent1,
        String   agent2,
        Instant  dateRecep,
        Instant  dateReco,
        Instant  dateContr,
        Instant  dateVerif,
        String   dispoTerrestre,
        String   dispoHbe,
        String   nature,
        String   typeHydrant,
        String[] anomalies,
        String   observation,
        String   auteurModification,
        Boolean  hbe,
        String   positionnement,
        String   materiau,
        String   volConstate,
        String   diametre,
        Integer  debit,
        Integer  debitMax,
        Double   pression,
        Double   pressionDyn,
        String   marque,
        String   modele,
        Double   pressionDynDeb,
        String   domaine,
        String   capacite,
        String   natureDeci,
        Integer  numeroVoie,
        String   suffixeVoie,
        String   niveau,
        String   gestionnaire,
        String   site,
        String   autoriteDeci,
        String   jumele,
        Boolean  dispositifInviolabilite,
        String   reservoir,
        String   serviceEaux,
        Boolean  debitRenforce,
        String   typeReseauCanalisation,
        String   typeReseauAlimentation,
        Integer  diametreCanalisation,
        Boolean  surpresse,
        Boolean  additive,
        Boolean  illimitee,
        Boolean  incertaine,
        Boolean  enFace,
        String   spDeci,
        Long     utilisateurModification,
        Long     organisme,
        String   auteurModificationFlag
    ) {
        this.id = id;
        this.numTransac = numTransac;
        this.nomOperation = nomOperation;
        this.dateOperation = dateOperation;
        this.idHydrant = idHydrant;
        this.numero = numero;
        this.geometrie = geometrie;
        this.insee = insee;
        this.commune = commune;
        this.lieuDit = lieuDit;
        this.voie = voie;
        this.carrefour = carrefour;
        this.complement = complement;
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.dateRecep = dateRecep;
        this.dateReco = dateReco;
        this.dateContr = dateContr;
        this.dateVerif = dateVerif;
        this.dispoTerrestre = dispoTerrestre;
        this.dispoHbe = dispoHbe;
        this.nature = nature;
        this.typeHydrant = typeHydrant;
        this.anomalies = anomalies;
        this.observation = observation;
        this.auteurModification = auteurModification;
        this.hbe = hbe;
        this.positionnement = positionnement;
        this.materiau = materiau;
        this.volConstate = volConstate;
        this.diametre = diametre;
        this.debit = debit;
        this.debitMax = debitMax;
        this.pression = pression;
        this.pressionDyn = pressionDyn;
        this.marque = marque;
        this.modele = modele;
        this.pressionDynDeb = pressionDynDeb;
        this.domaine = domaine;
        this.capacite = capacite;
        this.natureDeci = natureDeci;
        this.numeroVoie = numeroVoie;
        this.suffixeVoie = suffixeVoie;
        this.niveau = niveau;
        this.gestionnaire = gestionnaire;
        this.site = site;
        this.autoriteDeci = autoriteDeci;
        this.jumele = jumele;
        this.dispositifInviolabilite = dispositifInviolabilite;
        this.reservoir = reservoir;
        this.serviceEaux = serviceEaux;
        this.debitRenforce = debitRenforce;
        this.typeReseauCanalisation = typeReseauCanalisation;
        this.typeReseauAlimentation = typeReseauAlimentation;
        this.diametreCanalisation = diametreCanalisation;
        this.surpresse = surpresse;
        this.additive = additive;
        this.illimitee = illimitee;
        this.incertaine = incertaine;
        this.enFace = enFace;
        this.spDeci = spDeci;
        this.utilisateurModification = utilisateurModification;
        this.organisme = organisme;
        this.auteurModificationFlag = auteurModificationFlag;
    }

    public Integer getId() {
        return this.id;
    }

    public Hydrant setId(Integer id) {
        this.id = id;
        return this;
    }

    public Long getNumTransac() {
        return this.numTransac;
    }

    public Hydrant setNumTransac(Long numTransac) {
        this.numTransac = numTransac;
        return this;
    }

    public String getNomOperation() {
        return this.nomOperation;
    }

    public Hydrant setNomOperation(String nomOperation) {
        this.nomOperation = nomOperation;
        return this;
    }

    public Instant getDateOperation() {
        return this.dateOperation;
    }

    public Hydrant setDateOperation(Instant dateOperation) {
        this.dateOperation = dateOperation;
        return this;
    }

    public Long getIdHydrant() {
        return this.idHydrant;
    }

    public Hydrant setIdHydrant(Long idHydrant) {
        this.idHydrant = idHydrant;
        return this;
    }

    public String getNumero() {
        return this.numero;
    }

    public Hydrant setNumero(String numero) {
        this.numero = numero;
        return this;
    }


    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public Object getGeometrie() {
        return this.geometrie;
    }


    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public Hydrant setGeometrie(Object geometrie) {
        this.geometrie = geometrie;
        return this;
    }

    public String getInsee() {
        return this.insee;
    }

    public Hydrant setInsee(String insee) {
        this.insee = insee;
        return this;
    }

    public String getCommune() {
        return this.commune;
    }

    public Hydrant setCommune(String commune) {
        this.commune = commune;
        return this;
    }

    public String getLieuDit() {
        return this.lieuDit;
    }

    public Hydrant setLieuDit(String lieuDit) {
        this.lieuDit = lieuDit;
        return this;
    }

    public String getVoie() {
        return this.voie;
    }

    public Hydrant setVoie(String voie) {
        this.voie = voie;
        return this;
    }

    public String getCarrefour() {
        return this.carrefour;
    }

    public Hydrant setCarrefour(String carrefour) {
        this.carrefour = carrefour;
        return this;
    }

    public String getComplement() {
        return this.complement;
    }

    public Hydrant setComplement(String complement) {
        this.complement = complement;
        return this;
    }

    public String getAgent1() {
        return this.agent1;
    }

    public Hydrant setAgent1(String agent1) {
        this.agent1 = agent1;
        return this;
    }

    public String getAgent2() {
        return this.agent2;
    }

    public Hydrant setAgent2(String agent2) {
        this.agent2 = agent2;
        return this;
    }

    public Instant getDateRecep() {
        return this.dateRecep;
    }

    public Hydrant setDateRecep(Instant dateRecep) {
        this.dateRecep = dateRecep;
        return this;
    }

    public Instant getDateReco() {
        return this.dateReco;
    }

    public Hydrant setDateReco(Instant dateReco) {
        this.dateReco = dateReco;
        return this;
    }

    public Instant getDateContr() {
        return this.dateContr;
    }

    public Hydrant setDateContr(Instant dateContr) {
        this.dateContr = dateContr;
        return this;
    }

    public Instant getDateVerif() {
        return this.dateVerif;
    }

    public Hydrant setDateVerif(Instant dateVerif) {
        this.dateVerif = dateVerif;
        return this;
    }

    public String getDispoTerrestre() {
        return this.dispoTerrestre;
    }

    public Hydrant setDispoTerrestre(String dispoTerrestre) {
        this.dispoTerrestre = dispoTerrestre;
        return this;
    }

    public String getDispoHbe() {
        return this.dispoHbe;
    }

    public Hydrant setDispoHbe(String dispoHbe) {
        this.dispoHbe = dispoHbe;
        return this;
    }

    public String getNature() {
        return this.nature;
    }

    public Hydrant setNature(String nature) {
        this.nature = nature;
        return this;
    }

    public String getTypeHydrant() {
        return this.typeHydrant;
    }

    public Hydrant setTypeHydrant(String typeHydrant) {
        this.typeHydrant = typeHydrant;
        return this;
    }

    public String[] getAnomalies() {
        return this.anomalies;
    }

    public Hydrant setAnomalies(String... anomalies) {
        this.anomalies = anomalies;
        return this;
    }

    public String getObservation() {
        return this.observation;
    }

    public Hydrant setObservation(String observation) {
        this.observation = observation;
        return this;
    }

    public String getAuteurModification() {
        return this.auteurModification;
    }

    public Hydrant setAuteurModification(String auteurModification) {
        this.auteurModification = auteurModification;
        return this;
    }

    public Boolean getHbe() {
        return this.hbe;
    }

    public Hydrant setHbe(Boolean hbe) {
        this.hbe = hbe;
        return this;
    }

    public String getPositionnement() {
        return this.positionnement;
    }

    public Hydrant setPositionnement(String positionnement) {
        this.positionnement = positionnement;
        return this;
    }

    public String getMateriau() {
        return this.materiau;
    }

    public Hydrant setMateriau(String materiau) {
        this.materiau = materiau;
        return this;
    }

    public String getVolConstate() {
        return this.volConstate;
    }

    public Hydrant setVolConstate(String volConstate) {
        this.volConstate = volConstate;
        return this;
    }

    public String getDiametre() {
        return this.diametre;
    }

    public Hydrant setDiametre(String diametre) {
        this.diametre = diametre;
        return this;
    }

    public Integer getDebit() {
        return this.debit;
    }

    public Hydrant setDebit(Integer debit) {
        this.debit = debit;
        return this;
    }

    public Integer getDebitMax() {
        return this.debitMax;
    }

    public Hydrant setDebitMax(Integer debitMax) {
        this.debitMax = debitMax;
        return this;
    }

    public Double getPression() {
        return this.pression;
    }

    public Hydrant setPression(Double pression) {
        this.pression = pression;
        return this;
    }

    public Double getPressionDyn() {
        return this.pressionDyn;
    }

    public Hydrant setPressionDyn(Double pressionDyn) {
        this.pressionDyn = pressionDyn;
        return this;
    }

    public String getMarque() {
        return this.marque;
    }

    public Hydrant setMarque(String marque) {
        this.marque = marque;
        return this;
    }

    public String getModele() {
        return this.modele;
    }

    public Hydrant setModele(String modele) {
        this.modele = modele;
        return this;
    }

    public Double getPressionDynDeb() {
        return this.pressionDynDeb;
    }

    public Hydrant setPressionDynDeb(Double pressionDynDeb) {
        this.pressionDynDeb = pressionDynDeb;
        return this;
    }

    public String getDomaine() {
        return this.domaine;
    }

    public Hydrant setDomaine(String domaine) {
        this.domaine = domaine;
        return this;
    }

    public String getCapacite() {
        return this.capacite;
    }

    public Hydrant setCapacite(String capacite) {
        this.capacite = capacite;
        return this;
    }

    public String getNatureDeci() {
        return this.natureDeci;
    }

    public Hydrant setNatureDeci(String natureDeci) {
        this.natureDeci = natureDeci;
        return this;
    }

    public Integer getNumeroVoie() {
        return this.numeroVoie;
    }

    public Hydrant setNumeroVoie(Integer numeroVoie) {
        this.numeroVoie = numeroVoie;
        return this;
    }

    public String getSuffixeVoie() {
        return this.suffixeVoie;
    }

    public Hydrant setSuffixeVoie(String suffixeVoie) {
        this.suffixeVoie = suffixeVoie;
        return this;
    }

    public String getNiveau() {
        return this.niveau;
    }

    public Hydrant setNiveau(String niveau) {
        this.niveau = niveau;
        return this;
    }

    public String getGestionnaire() {
        return this.gestionnaire;
    }

    public Hydrant setGestionnaire(String gestionnaire) {
        this.gestionnaire = gestionnaire;
        return this;
    }

    public String getSite() {
        return this.site;
    }

    public Hydrant setSite(String site) {
        this.site = site;
        return this;
    }

    public String getAutoriteDeci() {
        return this.autoriteDeci;
    }

    public Hydrant setAutoriteDeci(String autoriteDeci) {
        this.autoriteDeci = autoriteDeci;
        return this;
    }

    public String getJumele() {
        return this.jumele;
    }

    public Hydrant setJumele(String jumele) {
        this.jumele = jumele;
        return this;
    }

    public Boolean getDispositifInviolabilite() {
        return this.dispositifInviolabilite;
    }

    public Hydrant setDispositifInviolabilite(Boolean dispositifInviolabilite) {
        this.dispositifInviolabilite = dispositifInviolabilite;
        return this;
    }

    public String getReservoir() {
        return this.reservoir;
    }

    public Hydrant setReservoir(String reservoir) {
        this.reservoir = reservoir;
        return this;
    }

    public String getServiceEaux() {
        return this.serviceEaux;
    }

    public Hydrant setServiceEaux(String serviceEaux) {
        this.serviceEaux = serviceEaux;
        return this;
    }

    public Boolean getDebitRenforce() {
        return this.debitRenforce;
    }

    public Hydrant setDebitRenforce(Boolean debitRenforce) {
        this.debitRenforce = debitRenforce;
        return this;
    }

    public String getTypeReseauCanalisation() {
        return this.typeReseauCanalisation;
    }

    public Hydrant setTypeReseauCanalisation(String typeReseauCanalisation) {
        this.typeReseauCanalisation = typeReseauCanalisation;
        return this;
    }

    public String getTypeReseauAlimentation() {
        return this.typeReseauAlimentation;
    }

    public Hydrant setTypeReseauAlimentation(String typeReseauAlimentation) {
        this.typeReseauAlimentation = typeReseauAlimentation;
        return this;
    }

    public Integer getDiametreCanalisation() {
        return this.diametreCanalisation;
    }

    public Hydrant setDiametreCanalisation(Integer diametreCanalisation) {
        this.diametreCanalisation = diametreCanalisation;
        return this;
    }

    public Boolean getSurpresse() {
        return this.surpresse;
    }

    public Hydrant setSurpresse(Boolean surpresse) {
        this.surpresse = surpresse;
        return this;
    }

    public Boolean getAdditive() {
        return this.additive;
    }

    public Hydrant setAdditive(Boolean additive) {
        this.additive = additive;
        return this;
    }

    public Boolean getIllimitee() {
        return this.illimitee;
    }

    public Hydrant setIllimitee(Boolean illimitee) {
        this.illimitee = illimitee;
        return this;
    }

    public Boolean getIncertaine() {
        return this.incertaine;
    }

    public Hydrant setIncertaine(Boolean incertaine) {
        this.incertaine = incertaine;
        return this;
    }

    public Boolean getEnFace() {
        return this.enFace;
    }

    public Hydrant setEnFace(Boolean enFace) {
        this.enFace = enFace;
        return this;
    }

    public String getSpDeci() {
        return this.spDeci;
    }

    public Hydrant setSpDeci(String spDeci) {
        this.spDeci = spDeci;
        return this;
    }

    public Long getUtilisateurModification() {
        return this.utilisateurModification;
    }

    public Hydrant setUtilisateurModification(Long utilisateurModification) {
        this.utilisateurModification = utilisateurModification;
        return this;
    }

    public Long getOrganisme() {
        return this.organisme;
    }

    public Hydrant setOrganisme(Long organisme) {
        this.organisme = organisme;
        return this;
    }

    public String getAuteurModificationFlag() {
        return this.auteurModificationFlag;
    }

    public Hydrant setAuteurModificationFlag(String auteurModificationFlag) {
        this.auteurModificationFlag = auteurModificationFlag;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Hydrant (");

        sb.append(id);
        sb.append(", ").append(numTransac);
        sb.append(", ").append(nomOperation);
        sb.append(", ").append(dateOperation);
        sb.append(", ").append(idHydrant);
        sb.append(", ").append(numero);
        sb.append(", ").append(geometrie);
        sb.append(", ").append(insee);
        sb.append(", ").append(commune);
        sb.append(", ").append(lieuDit);
        sb.append(", ").append(voie);
        sb.append(", ").append(carrefour);
        sb.append(", ").append(complement);
        sb.append(", ").append(agent1);
        sb.append(", ").append(agent2);
        sb.append(", ").append(dateRecep);
        sb.append(", ").append(dateReco);
        sb.append(", ").append(dateContr);
        sb.append(", ").append(dateVerif);
        sb.append(", ").append(dispoTerrestre);
        sb.append(", ").append(dispoHbe);
        sb.append(", ").append(nature);
        sb.append(", ").append(typeHydrant);
        sb.append(", ").append(Arrays.toString(anomalies));
        sb.append(", ").append(observation);
        sb.append(", ").append(auteurModification);
        sb.append(", ").append(hbe);
        sb.append(", ").append(positionnement);
        sb.append(", ").append(materiau);
        sb.append(", ").append(volConstate);
        sb.append(", ").append(diametre);
        sb.append(", ").append(debit);
        sb.append(", ").append(debitMax);
        sb.append(", ").append(pression);
        sb.append(", ").append(pressionDyn);
        sb.append(", ").append(marque);
        sb.append(", ").append(modele);
        sb.append(", ").append(pressionDynDeb);
        sb.append(", ").append(domaine);
        sb.append(", ").append(capacite);
        sb.append(", ").append(natureDeci);
        sb.append(", ").append(numeroVoie);
        sb.append(", ").append(suffixeVoie);
        sb.append(", ").append(niveau);
        sb.append(", ").append(gestionnaire);
        sb.append(", ").append(site);
        sb.append(", ").append(autoriteDeci);
        sb.append(", ").append(jumele);
        sb.append(", ").append(dispositifInviolabilite);
        sb.append(", ").append(reservoir);
        sb.append(", ").append(serviceEaux);
        sb.append(", ").append(debitRenforce);
        sb.append(", ").append(typeReseauCanalisation);
        sb.append(", ").append(typeReseauAlimentation);
        sb.append(", ").append(diametreCanalisation);
        sb.append(", ").append(surpresse);
        sb.append(", ").append(additive);
        sb.append(", ").append(illimitee);
        sb.append(", ").append(incertaine);
        sb.append(", ").append(enFace);
        sb.append(", ").append(spDeci);
        sb.append(", ").append(utilisateurModification);
        sb.append(", ").append(organisme);
        sb.append(", ").append(auteurModificationFlag);

        sb.append(")");
        return sb.toString();
    }
}