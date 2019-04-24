package fr.sdis83.remocra.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;

import fr.sdis83.remocra.xml.HydrantAspirationIndetermine;
import fr.sdis83.remocra.xml.HydrantChateauEau;
import fr.sdis83.remocra.xml.HydrantCiterneEn;
import fr.sdis83.remocra.xml.HydrantCiterneAerienne;
import fr.sdis83.remocra.xml.HydrantPointAspiration;
import fr.sdis83.remocra.xml.HydrantPoteauRelais;
import fr.sdis83.remocra.xml.HydrantPuitPuisard;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.vividsolutions.jts.geom.Point;

import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantDocument;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalie;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalieNature;
import fr.sdis83.remocra.domain.remocra.TypeHydrantDiametre;
import fr.sdis83.remocra.domain.remocra.TypeHydrantDomaine;
import fr.sdis83.remocra.domain.remocra.TypeHydrantMarque;
import fr.sdis83.remocra.domain.remocra.TypeHydrantMateriau;
import fr.sdis83.remocra.domain.remocra.TypeHydrantModele;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNatureDeci;
import fr.sdis83.remocra.domain.remocra.TypeHydrantPositionnement;
import fr.sdis83.remocra.domain.remocra.TypeHydrantSaisie;
import fr.sdis83.remocra.domain.remocra.TypeHydrantVolConstate;
import fr.sdis83.remocra.exception.AnomalieException;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.exception.SQLBusinessException;
import fr.sdis83.remocra.exception.XmlDroitException;
import fr.sdis83.remocra.exception.XmlValidationException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.NumeroUtil;
import fr.sdis83.remocra.util.XmlUtil;
import fr.sdis83.remocra.xml.Anomalie;
import fr.sdis83.remocra.xml.AnomalieNature;
import fr.sdis83.remocra.xml.Coordonnee;
import fr.sdis83.remocra.xml.Diametre;
import fr.sdis83.remocra.xml.HydrantBi;
import fr.sdis83.remocra.xml.HydrantCiterneEnterre;
import fr.sdis83.remocra.xml.HydrantCiterneFixe;
import fr.sdis83.remocra.xml.HydrantCoursEau;
import fr.sdis83.remocra.xml.HydrantPa;
import fr.sdis83.remocra.xml.HydrantPena;
import fr.sdis83.remocra.xml.HydrantPi;
import fr.sdis83.remocra.xml.HydrantPibi;
import fr.sdis83.remocra.xml.HydrantPlanEau;
import fr.sdis83.remocra.xml.HydrantPuisard;
import fr.sdis83.remocra.xml.HydrantReserveIncendie;
import fr.sdis83.remocra.xml.HydrantRetenue;
import fr.sdis83.remocra.xml.LstAnomalies;
import fr.sdis83.remocra.xml.LstAnomaliesNatures;
import fr.sdis83.remocra.xml.LstCommunes;
import fr.sdis83.remocra.xml.LstDiametres;
import fr.sdis83.remocra.xml.LstDomaines;
import fr.sdis83.remocra.xml.LstHydrants;
import fr.sdis83.remocra.xml.LstMarques;
import fr.sdis83.remocra.xml.LstMateriaux;
import fr.sdis83.remocra.xml.LstModeles;
import fr.sdis83.remocra.xml.LstNatures;
import fr.sdis83.remocra.xml.LstNaturesDeci;
import fr.sdis83.remocra.xml.LstPositionnements;
import fr.sdis83.remocra.xml.LstSaisies;
import fr.sdis83.remocra.xml.LstTournees;
import fr.sdis83.remocra.xml.LstVolConstates;
import fr.sdis83.remocra.xml.Marque;
import fr.sdis83.remocra.xml.Modele;
import fr.sdis83.remocra.xml.Nature;
import fr.sdis83.remocra.xml.NatureDeci;
import fr.sdis83.remocra.xml.Referentiel;
import fr.sdis83.remocra.xml.Saisie;

@Configuration
public class XmlService {

    private static final String TYPE_HYDRANT_PIBI = "PIBI";

    private static final String TYPE_HYDRANT_PENA = "PENA";

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ParamConfService paramConfService;

    @Autowired
    private TourneeService tourneeService;

    @Autowired
    private AuthoritiesUtil authUtils;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private ZoneCompetenceService zoneCompetenceService;

    @Autowired
    private UtilisateurService utilisateurService;

    public LstCommunes getCommunes() {

        List<Commune> lstCommune = Commune.findAllCommunes();

        ArrayList<fr.sdis83.remocra.xml.Commune> lstFinalCommunes = new ArrayList<fr.sdis83.remocra.xml.Commune>();
        for (Commune commune : lstCommune) {
            lstFinalCommunes.add(new fr.sdis83.remocra.xml.Commune(commune.getCode(), commune.getNom(), commune.getInsee()));
        }

        LstCommunes lstCommunesXML = new LstCommunes();
        lstCommunesXML.setCommunes(lstFinalCommunes);

        return lstCommunesXML;
    }

    public void serializeCommunes(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstCommunes.class, getCommunes(), "communes", out);
    }

    public LstAnomalies getAnomalies() {

        List<TypeHydrantAnomalie> lstAnomalie = TypeHydrantAnomalie.findTypeHydrantAnomaliesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Anomalie> lstFinalAnomalies = new ArrayList<fr.sdis83.remocra.xml.Anomalie>();

        for (TypeHydrantAnomalie anomalie : lstAnomalie) {

            Anomalie anomalieXML = new fr.sdis83.remocra.xml.Anomalie();

            anomalieXML.setCode(anomalie.getCode());
            anomalieXML.setLibelle(anomalie.getNom());
            anomalieXML.setCritere(anomalie.getCritere() != null ? anomalie.getCritere().getNom() : "");

            Set<TypeHydrantAnomalieNature> anomalieNatures = anomalie.getAnomalieNatures();

            ArrayList<AnomalieNature> lstFinalAnomalieNatures = new ArrayList<AnomalieNature>();

            for (TypeHydrantAnomalieNature typeHydrantAnomalieNature : anomalieNatures) {

                AnomalieNature anomalieNatureXML = new AnomalieNature();
                anomalieNatureXML.setValeur(typeHydrantAnomalieNature.getValIndispoTerrestre());
                anomalieNatureXML.setValeurAdmin(typeHydrantAnomalieNature.getValIndispoAdmin());
                anomalieNatureXML.setCodeNature(typeHydrantAnomalieNature.getNature().getCode());

                Set<TypeHydrantSaisie> lstAnomalieNatureSaisie = typeHydrantAnomalieNature.getSaisies();

                ArrayList<Saisie> lstFinalSaisie = new ArrayList<Saisie>();

                for (TypeHydrantSaisie typeHydrantSaisie : lstAnomalieNatureSaisie) {
                    Saisie saisieXML = new Saisie();
                    saisieXML.setCode(typeHydrantSaisie.getCode());
                    lstFinalSaisie.add(saisieXML);
                }
                LstSaisies lstSaisieXML = new LstSaisies();
                lstSaisieXML.setSaisies(lstFinalSaisie);
                anomalieNatureXML.setSaisies(lstSaisieXML);

                lstFinalAnomalieNatures.add(anomalieNatureXML);
            }

            LstAnomaliesNatures lstAnomaliesNatures = new LstAnomaliesNatures();
            lstAnomaliesNatures.setAnomaliesNatures(lstFinalAnomalieNatures);
            anomalieXML.setAnomaliesNatures(lstAnomaliesNatures);

            lstFinalAnomalies.add(anomalieXML);
        }

        LstAnomalies lstAnomaliesXML = new LstAnomalies();
        lstAnomaliesXML.setAnomalies(lstFinalAnomalies);

        return lstAnomaliesXML;
    }

    public void serializeAnomalies(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstAnomalies.class, getAnomalies(), "anomalies", out);
    }

    public LstDiametres getDiametres() {

        List<TypeHydrantDiametre> lstDiametre = TypeHydrantDiametre.findTypeHydrantDiametresByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Diametre> lstFinalDiametres = new ArrayList<fr.sdis83.remocra.xml.Diametre>();
        for (TypeHydrantDiametre diametre : lstDiametre) {
            Diametre diametreXML = new fr.sdis83.remocra.xml.Diametre(diametre.getCode(), diametre.getNom());
            Set<TypeHydrantNature> natures = diametre.getNatures();

            ArrayList<Nature> lstNatures = new ArrayList<Nature>();

            for (TypeHydrantNature typeHydrantNature : natures) {
                Nature natureXML = new Nature();
                natureXML.setCode(typeHydrantNature.getCode());
                lstNatures.add(natureXML);
            }

            LstNatures naturesXML = new LstNatures();
            naturesXML.setNatures(lstNatures);
            diametreXML.setNatures(naturesXML);

            lstFinalDiametres.add(diametreXML);
        }

        LstDiametres lstDiametresXML = new LstDiametres();
        lstDiametresXML.setDiametres(lstFinalDiametres);

        return lstDiametresXML;
    }

    public void serializeDiametres(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstDiametres.class, getDiametres(), "diamètres", out);
    }

    public LstDomaines getDomaines() {

        List<TypeHydrantDomaine> lstDomaine = TypeHydrantDomaine.findTypeHydrantDomainesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Domaine> lstFinalDomaines = new ArrayList<fr.sdis83.remocra.xml.Domaine>();
        for (TypeHydrantDomaine domaine : lstDomaine) {
            lstFinalDomaines.add(new fr.sdis83.remocra.xml.Domaine(domaine.getCode(), domaine.getNom()));
        }

        LstDomaines lstDomainesXML = new LstDomaines();
        lstDomainesXML.setDomaines(lstFinalDomaines);

        return lstDomainesXML;
    }

    public void serializeDomaines(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstDomaines.class, getDomaines(), "domaines", out);
    }

    public LstPositionnements getPositionnements() {

        List<TypeHydrantPositionnement> lstPositionnement = TypeHydrantPositionnement.findTypeHydrantPositionnementsByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Positionnement> lstFinalPositionnements = new ArrayList<fr.sdis83.remocra.xml.Positionnement>();
        for (TypeHydrantPositionnement positionnement : lstPositionnement) {
            lstFinalPositionnements.add(new fr.sdis83.remocra.xml.Positionnement(positionnement.getCode(), positionnement.getNom()));
        }

        LstPositionnements lstPositionnementsXML = new LstPositionnements();
        lstPositionnementsXML.setPositionnements(lstFinalPositionnements);

        return lstPositionnementsXML;
    }

    public void serializePositionnements(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstPositionnements.class, getPositionnements(), "postionnements", out);
    }

    public LstMateriaux getMateriaux() {

        List<TypeHydrantMateriau> lstMateriau = TypeHydrantMateriau.findTypeHydrantMateriausByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Materiau> lstFinalMateriaux = new ArrayList<fr.sdis83.remocra.xml.Materiau>();
        for (TypeHydrantMateriau materiau : lstMateriau) {
            lstFinalMateriaux.add(new fr.sdis83.remocra.xml.Materiau(materiau.getCode(), materiau.getNom()));
        }

        LstMateriaux lstMateriauxXML = new LstMateriaux();
        lstMateriauxXML.setMateriaux(lstFinalMateriaux);

        return lstMateriauxXML;
    }

    public void serializeMateriaux(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstMateriaux.class, getMateriaux(), "matériaux", out);
    }

    public LstNatures getNatures() {

        List<TypeHydrantNature> lstNature = TypeHydrantNature.findTypeHydrantNaturesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Nature> lstFinalNatures = new ArrayList<fr.sdis83.remocra.xml.Nature>();
        for (TypeHydrantNature nature : lstNature) {
            lstFinalNatures.add(new fr.sdis83.remocra.xml.Nature(nature.getCode(), nature.getNom(), nature.getTypeHydrant() != null ? nature.getTypeHydrant().getNom() : ""));
        }

        LstNatures lstNaturesXML = new LstNatures();
        lstNaturesXML.setNatures(lstFinalNatures);

        return lstNaturesXML;
    }

    public void serializeNatures(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstNatures.class, getNatures(), "natures", out);
    }

    public LstModeles getModeles() {

        List<TypeHydrantModele> lstModele = TypeHydrantModele.findTypeHydrantModelesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Modele> lstFinalModeles = new ArrayList<fr.sdis83.remocra.xml.Modele>();
        for (TypeHydrantModele modele : lstModele) {
            lstFinalModeles.add(new fr.sdis83.remocra.xml.Modele(modele.getCode(), modele.getNom()));
        }

        LstModeles lstModelesXML = new LstModeles();
        lstModelesXML.setModeles(lstFinalModeles);

        return lstModelesXML;
    }

    public void serializeModeles(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstModeles.class, getModeles(), "modèles", out);
    }

    public LstMarques getMarques() {

        List<TypeHydrantMarque> lstMarque = TypeHydrantMarque.findTypeHydrantMarquesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Marque> lstFinalMarques = new ArrayList<fr.sdis83.remocra.xml.Marque>();

        for (TypeHydrantMarque marque : lstMarque) {
            Marque marqueXML = new Marque(marque.getCode(), marque.getNom());

            ArrayList<Modele> lstModelesXML = new ArrayList<Modele>();

            Set<TypeHydrantModele> lstModeles = marque.getModeles();
            for (TypeHydrantModele typeHydrantModele : lstModeles) {
                Modele modeleXML = new Modele(typeHydrantModele.getCode(), typeHydrantModele.getNom());
                lstModelesXML.add(modeleXML);
            }

            LstModeles lstModelesXMLFinale = new LstModeles();
            lstModelesXMLFinale.setModeles(lstModelesXML);

            marqueXML.setModeles(lstModelesXMLFinale);
            lstFinalMarques.add(marqueXML);
        }

        LstMarques lstMarquesXML = new LstMarques();
        lstMarquesXML.setMarques(lstFinalMarques);

        return lstMarquesXML;
    }

    public void serializeMarques(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstMarques.class, getMarques(), "marques", out);
    }

    public LstVolConstates getVolConstates() {
        List<TypeHydrantVolConstate> lstVolConstate = TypeHydrantVolConstate.findTypeHydrantVolConstatesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.VolConstate> lstFinalVolConstates = new ArrayList<fr.sdis83.remocra.xml.VolConstate>();
        for (TypeHydrantVolConstate volConstate : lstVolConstate) {
            lstFinalVolConstates.add(new fr.sdis83.remocra.xml.VolConstate(volConstate.getCode(), volConstate.getNom()));
        }

        LstVolConstates lstVolConstatesXML = new LstVolConstates();
        lstVolConstatesXML.setVolConstates(lstFinalVolConstates);

        return lstVolConstatesXML;
    }

    public void serializeVolConstates(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstVolConstates.class, getVolConstates(), "vols constates", out);
    }

    public LstSaisies getSaisies() {
        List<TypeHydrantSaisie> lstSaisie = TypeHydrantSaisie.findTypeHydrantSaisiesByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.Saisie> lstFinalSaisies = new ArrayList<fr.sdis83.remocra.xml.Saisie>();
        for (TypeHydrantSaisie saisie : lstSaisie) {
            lstFinalSaisies.add(new fr.sdis83.remocra.xml.Saisie(saisie.getCode(), saisie.getNom()));
        }

        LstSaisies lstSaisieXML = new LstSaisies();
        lstSaisieXML.setSaisies(lstFinalSaisies);

        return lstSaisieXML;
    }

    public void serializeSaisies(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstSaisies.class, getSaisies(), "saisies", out);
    }

    public LstNaturesDeci getNaturesDeci() {

        List<TypeHydrantNatureDeci> lstNaturesDeci = TypeHydrantNatureDeci.findTypeHydrantNatureDecisByActif(true).getResultList();

        ArrayList<fr.sdis83.remocra.xml.NatureDeci> lstFinalNaturesDeci = new ArrayList<NatureDeci>();
        for (TypeHydrantNatureDeci natureDeci : lstNaturesDeci) {
            lstFinalNaturesDeci.add(new fr.sdis83.remocra.xml.NatureDeci(natureDeci.getCode(), natureDeci.getNom()));
        }

        LstNaturesDeci lstNaturesDeciXML = new LstNaturesDeci();
        lstNaturesDeciXML.setNaturesDeci(lstFinalNaturesDeci);

        return lstNaturesDeciXML;
    }

    public void serializeNaturesDeci(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstNaturesDeci.class, getNaturesDeci(), "naturesDeci", out);
    }

    public Referentiel getReferentiels() {

        Referentiel referentiels = new Referentiel();

        referentiels.setAnomalies(this.getAnomalies());
        referentiels.setCommunes(this.getCommunes());
        referentiels.setDiametres(this.getDiametres());
        referentiels.setDomaines(this.getDomaines());
        referentiels.setMarques(this.getMarques());
        referentiels.setMateriaux(this.getMateriaux());
        referentiels.setNatures(this.getNatures());
        referentiels.setPositionnements(this.getPositionnements());
        referentiels.setVolConstates(this.getVolConstates());
        referentiels.setNaturesDeci(this.getNaturesDeci());

        return referentiels;
    }

    public void serializeReferentiels(OutputStream out) throws BusinessException, SQLBusinessException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.Referentiel.class, getReferentiels(), "référentiels", out);
    }

    public LstTournees getTournees(List<Long> idTournees, boolean lock) throws IOException, CRSException, IllegalCoordinateException {
        LstTournees lstTourneesXML = new LstTournees();
        if (idTournees == null || idTournees.size() == 0) {
            // on "relâche" les tournées de l'utilisateur
            Query qUpdate = entityManager.createQuery("UPDATE Tournee t set t.reservation = null where t.reservation = :user");
            qUpdate.setParameter("user", utilisateurService.getCurrentUtilisateur());
            int nbTournee = qUpdate.executeUpdate();
            logger.debug("Nombre de tournées relâchées : " + nbTournee);
            return lstTourneesXML;
        }

        TypedQuery<Tournee> query = entityManager
                .createQuery("SELECT t FROM Tournee t "
                        + " where t.id IN :ids "
                        + " and t.affectation = :organisme "
                        + (lock ? " and (t.reservation is null OR t.reservation = :user) " : "") +
        // Pour permettre à l'utilisateur qui le souhaite de conserver
        // sa tournée, on commente le filtre sur l'état de la tournée :
        // + " and t.etat < 100 "
                        " and t.hydrantCount > 0", Tournee.class);
        query.setParameter("ids", idTournees);
        query.setParameter("organisme", utilisateurService.getCurrentUtilisateur().getOrganisme());
        if (lock) {
            query.setParameter("user", utilisateurService.getCurrentUtilisateur());
        }

        List<Tournee> lstTournees = query.getResultList();

        ArrayList<fr.sdis83.remocra.xml.Tournee> lstTourneesFinal = new ArrayList<fr.sdis83.remocra.xml.Tournee>();
        for (Tournee tournee : lstTournees) {

            fr.sdis83.remocra.xml.Tournee tourneeXML = new fr.sdis83.remocra.xml.Tournee();
            tourneeXML.setId(tournee.getId());
            tourneeXML.setNom(tournee.getNom());
            tourneeXML.setPourcent(tournee.getEtat());
            tourneeXML.setDebSync(tournee.getDebSync());
            if (tourneeXML.getPourcent() == 0){
                tourneeXML.setDebSync(new Date());
            }
            tourneeXML.setLastSync(tournee.getLastSync());

            List<Hydrant> lstHydrants = tourneeService.getHydrants(tournee.getId());

            LstHydrants lstHydrantsXML = new LstHydrants();

            ArrayList<HydrantPibi> lsthydrantsPibiXML = new ArrayList<HydrantPibi>();
            ArrayList<HydrantPena> lsthydrantsPenaXML = new ArrayList<HydrantPena>();

            ArrayList<fr.sdis83.remocra.xml.Hydrant> lsthydrants = new ArrayList<fr.sdis83.remocra.xml.Hydrant>();

            for (Hydrant hydrant : lstHydrants) {
                if (TYPE_HYDRANT_PIBI.equals(hydrant.getCode())) {
                    HydrantPibi hydrantPibiXML = null;
                    if (hydrant.getNature().getCode().equals(HydrantPibi.CODE_NATURE_PI)) {
                        hydrantPibiXML = new HydrantPi();
                    } else if (hydrant.getNature().getCode().equals(HydrantPibi.CODE_NATURE_BI)) {
                        hydrantPibiXML = new HydrantBi();
                    } else if(HydrantPibi.CODE_NATURE_PA.equals(hydrant.getNature().getCode())) {
                        hydrantPibiXML = new HydrantPa();
                    }
                    fillHydrantPibi(hydrantPibiXML, (fr.sdis83.remocra.domain.remocra.HydrantPibi) hydrant);
                    lsthydrantsPibiXML.add(hydrantPibiXML);
                    lsthydrants.add(hydrantPibiXML);
                } else if (TYPE_HYDRANT_PENA.equals(hydrant.getCode())) {
                    HydrantPena hydrantPenaXML = null;
                    if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CE)) {
                        hydrantPenaXML = new HydrantCoursEau();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CI_ENTERRE)) {
                        hydrantPenaXML = new HydrantCiterneEnterre();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CI_EN)) {
                        hydrantPenaXML = new HydrantCiterneEn();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CI_AE)) {
                        hydrantPenaXML = new HydrantCiterneAerienne();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CI_FIXE)) {
                        hydrantPenaXML = new HydrantCiterneFixe();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_PE)) {
                        hydrantPenaXML = new HydrantPlanEau();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_PU)) {
                        hydrantPenaXML = new HydrantPuisard();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_RE)) {
                        hydrantPenaXML = new HydrantRetenue();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_RI)) {
                        hydrantPenaXML = new HydrantReserveIncendie();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_PUI)) {
                        hydrantPenaXML = new HydrantPuitPuisard();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_PA_I)) {
                        hydrantPenaXML = new HydrantPointAspiration();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_ASP_I)) {
                        hydrantPenaXML = new HydrantAspirationIndetermine();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_CHE)) {
                        hydrantPenaXML = new HydrantChateauEau();
                    } else if (hydrant.getNature().getCode().equals(HydrantPena.CODE_NATURE_PR)) {
                        hydrantPenaXML = new HydrantPoteauRelais();
                    }
                    fillHydrantPena(hydrantPenaXML, (fr.sdis83.remocra.domain.remocra.HydrantPena) hydrant);
                    lsthydrantsPenaXML.add(hydrantPenaXML);
                    lsthydrants.add(hydrantPenaXML);
                }

            }

            lstHydrantsXML.setHydrantsPibi(lsthydrantsPibiXML);
            lstHydrantsXML.setHydrantsPena(lsthydrantsPenaXML);

            tourneeXML.setHydrants(lstHydrantsXML);

            lstTourneesFinal.add(tourneeXML);
            if (lock) {
                // on "réserve" la tournée pour l'utilisateur
                tournee.setReservation(utilisateurService.getCurrentUtilisateur());
                tournee.setDebSync(tourneeXML.getDebSync());
                tournee.merge();
            }
        }
        lstTourneesXML.setTournees(lstTourneesFinal);
        if (lock) {
            // on "relâche" les tournées de l'utilisateur qui n'ont pas été
            // sélectionnées.
            Query qUpdate = entityManager.createQuery("UPDATE Tournee t set t.reservation = null where t.reservation = :user AND t.id NOT IN :ids");
            qUpdate.setParameter("ids", idTournees);
            qUpdate.setParameter("user", utilisateurService.getCurrentUtilisateur());
            int nbTournee = qUpdate.executeUpdate();
            logger.debug("Nombre de tournées relâchées : " + nbTournee);
        }

        return lstTourneesXML;
    }

    public void serializeTournees(List<Long> idTournees, OutputStream out, boolean lock) throws BusinessException, SQLBusinessException, IOException, CRSException, IllegalCoordinateException {
        serializeXmlExceptionManaged(fr.sdis83.remocra.xml.LstTournees.class, getTournees(idTournees, lock), "tournées", out);
    }

    public void fillHydrant(fr.sdis83.remocra.xml.Hydrant hydrantXML, Hydrant hydrant) throws IOException, CRSException, IllegalCoordinateException {
        hydrantXML.setAgent1(null);
        hydrantXML.setAgent2(null);
        hydrantXML.setAnneeFabrication(hydrant.getAnneeFabrication());
        hydrantXML.setCodeCommune(hydrant.getCommune() != null ? hydrant.getCommune().getCode() : "");
        hydrantXML.setCodeDomaine(hydrant.getDomaine() != null ? hydrant.getDomaine().getCode() : "");
        hydrantXML.setComplement(hydrant.getComplement());
        hydrantXML.setDateContr(hydrant.getDateContr());
        hydrantXML.setDateGps(hydrant.getDateGps());
        hydrantXML.setDateModification(hydrant.getDateModification());
        hydrantXML.setDateRecep(hydrant.getDateRecep());
        hydrantXML.setDateReco(hydrant.getDateReco());
        hydrantXML.setDateVerif(hydrant.getDateVerif());
        hydrantXML.setDispoTerrestre(hydrant.getDispoTerrestre() != null ? hydrant.getDispoTerrestre().toString() : "");
        hydrantXML.setLieuDit(hydrant.getLieuDit());
        hydrantXML.setNumero(hydrant.getNumero());
        hydrantXML.setNumeroInterne(hydrant.getNumeroInterne());
        hydrantXML.setVoie(hydrant.getVoie());
        hydrantXML.setVoie2(hydrant.getVoie2());
        hydrantXML.setObservation(hydrant.getObservation());
        hydrantXML.setCourrier(hydrant.getCourrier());
        hydrantXML.setGestPointEau(hydrant.getGestPointEau());
        hydrantXML.setDateAttestation(hydrant.getDateAttestation());
        hydrantXML.setCodeNatureDeci(hydrant.getNatureDeci() != null ? hydrant.getNatureDeci().getCode() : "");

        if (hydrant.getHydrantDocuments().size() > 0) {

            HydrantDocument photo = hydrant.getPhoto();
            if (photo != null) {
                Document document = hydrant.getPhoto().getDocument();
                File file = new File(document.getRepertoire() + File.separator + document.getFichier());
                BufferedImage originalImage = ImageIO.read(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();
                String img64 = Base64.encodeBase64String(imageInByte);
                hydrantXML.setPhoto(img64);
            }
        }

        Set<TypeHydrantAnomalie> lstAnomalies = hydrant.getAnomalies();

        ArrayList<Anomalie> lstAnomalieFinal = new ArrayList<Anomalie>();

        for (TypeHydrantAnomalie typeHydrantAnomalie : lstAnomalies) {
            Anomalie anomalie = new Anomalie();
            anomalie.setCode(typeHydrantAnomalie.getCode());
            lstAnomalieFinal.add(anomalie);
        }
        LstAnomalies lstAnomaliexXML = new LstAnomalies();
        lstAnomaliexXML.setAnomalies(lstAnomalieFinal);
        hydrantXML.setAnomalies(lstAnomaliexXML);

        if (hydrant.getGeometrie() != null) {
            double[] coordonneConvert = GeometryUtil.transformCordinate(hydrant.getGeometrie().getX(), hydrant.getGeometrie().getY(), "2154", "4326");
            hydrantXML.setCoordonnee(new Coordonnee(coordonneConvert[0], coordonneConvert[1]));
        }

    }

    public void fillHydrantPibi(fr.sdis83.remocra.xml.HydrantPibi hydrantPibiXML, fr.sdis83.remocra.domain.remocra.HydrantPibi hydrantPibi)
            throws IOException, CRSException, IllegalCoordinateException {
        fillHydrant(hydrantPibiXML, hydrantPibi);
        hydrantPibiXML.setRenversable(hydrantPibi.getRenversable());
        hydrantPibiXML.setCodeDiametre(hydrantPibi.getDiametre() != null ? hydrantPibi.getDiametre().getCode() : "");
        hydrantPibiXML.setCodeMarque(hydrantPibi.getMarque() != null ? hydrantPibi.getMarque().getCode() : "");
        hydrantPibiXML.setCodeModele(hydrantPibi.getModele() != null ? hydrantPibi.getModele().getCode() : "");
        hydrantPibiXML.setDebit(hydrantPibi.getDebit());
        hydrantPibiXML.setDebitMax(hydrantPibi.getDebitMax());
        hydrantPibiXML.setGestReseau(hydrantPibi.getGestReseau());
        hydrantPibiXML.setNumeroSCP(hydrantPibi.getNumeroSCP());
        hydrantPibiXML.setPression(hydrantPibi.getPression());
        hydrantPibiXML.setPressionDyn(hydrantPibi.getPressionDyn());
        hydrantPibiXML.setPressionDynDeb(hydrantPibi.getPressionDynDeb());
    }

    public void fillHydrantPena(fr.sdis83.remocra.xml.HydrantPena hydrantPenaXML, fr.sdis83.remocra.domain.remocra.HydrantPena hydrantPena)
            throws IOException, CRSException, IllegalCoordinateException {
        fillHydrant(hydrantPenaXML, hydrantPena);
        hydrantPenaXML.setCoordDFCI(hydrantPena.getCoordDFCI());

        hydrantPenaXML.setCapacite(hydrantPena.getCapacite());
        hydrantPenaXML.setDispoHbe(hydrantPena.getDispoHbe() != null ? hydrantPena.getDispoHbe().toString() : "");
        hydrantPenaXML.setHbe(hydrantPena.getHbe().booleanValue());

        if (hydrantPenaXML instanceof HydrantCiterneEnterre) {
            fillHydrantCiterne((HydrantCiterneEnterre) hydrantPenaXML, hydrantPena);
        } else if (hydrantPenaXML instanceof HydrantCiterneFixe) {
            fillHydrantCiterne((HydrantCiterneEnterre) hydrantPenaXML, hydrantPena);
            ((HydrantCiterneFixe) hydrantPenaXML).setCodePositionnement(hydrantPena.getPositionnement() != null ? hydrantPena.getPositionnement().getCode() : "");
        }
    }

    public void fillHydrantCiterne(HydrantCiterneEnterre hydrantPenaXML, fr.sdis83.remocra.domain.remocra.HydrantPena hydrantPena) {
        hydrantPenaXML.setCodeMateriau(hydrantPena.getMateriau() != null ? hydrantPena.getMateriau().getCode() : "");
        hydrantPenaXML.setCodeVolConstate(hydrantPena.getVolConstate() != null ? hydrantPena.getVolConstate().getCode() : "");
        hydrantPenaXML.setqAppoint(hydrantPena.getQAppoint());
    }

    @Transactional
    public void deSerializeHydrants(String xml, Integer version) throws BusinessException, XmlValidationException, SQLBusinessException, XmlDroitException, AnomalieException {
        try {
            LstHydrants hydrants = (LstHydrants) XmlUtil.unSerializeXml(xml, fr.sdis83.remocra.xml.LstHydrants.class,"fr/sdis83/remocra/service/xml/Hydrants.xsd");

            for (HydrantPena hydrant : hydrants.getHydrantsPena()) {
                fr.sdis83.remocra.domain.remocra.HydrantPena hydrantPena = null;

                if (hydrant.getNumero() != null) {
                    try {
                        hydrantPena = (fr.sdis83.remocra.domain.remocra.HydrantPena) fr.sdis83.remocra.domain.remocra.HydrantPena.findHydrantsByNumero(hydrant.getNumero())
                            .getSingleResult();
                    } catch (Exception e) {
                        logger.warn("Hydrant non trouvé : " + hydrant.getNumero());
                        continue;
                    }
                } else {
                    hydrantPena = new fr.sdis83.remocra.domain.remocra.HydrantPena();
                    Coordonnee coordonnee = hydrant.getCoordonnee();
                    Point point = GeometryUtil.createPoint(coordonnee.getLongitude(), coordonnee.getLatitude(), "4326", "2154");
                    hydrantPena.setGeometrie(point);
                    hydrantPena.setDateGps(null);
                }
                // Par sécurité
                hydrantPena.setCode(TYPE_HYDRANT_PENA);

                updateHydrant(hydrantPena, hydrant, version);
            }

            for (HydrantPibi hydrant : hydrants.getHydrantsPibi()) {
                fr.sdis83.remocra.domain.remocra.HydrantPibi hydrantPibi = null;

                if ((hydrant.getNumero() != null)) {
                    try {
                        hydrantPibi = (fr.sdis83.remocra.domain.remocra.HydrantPibi) fr.sdis83.remocra.domain.remocra.HydrantPibi.findHydrantsByNumero(hydrant.getNumero())
                            .getSingleResult();
                    }catch(Exception e ){
                        logger.warn("Hydrant non trouvé : " + hydrant.getNumero());
                        continue;
                    }
                } else {
                    hydrantPibi = new fr.sdis83.remocra.domain.remocra.HydrantPibi();
                    Coordonnee coordonnee = hydrant.getCoordonnee();
                    Point point = GeometryUtil.createPoint(coordonnee.getLongitude(), coordonnee.getLatitude(), "4326", "2154");
                    hydrantPibi.setGeometrie(point);
                    hydrantPibi.setDateGps(null);
                }
                // Par sécurité
                hydrantPibi.setCode(TYPE_HYDRANT_PIBI);

                updateHydrant(hydrantPibi, hydrant, version);
            }
        } catch (SAXException e) {
            SAXParseException nested = ExceptionUtils.getNestedExceptionWithClass(e, SAXParseException.class);
            if (nested != null) {
                logger.error("Problème avec la validation XML des hydrants : " + nested.getMessage() + nested.getLineNumber() + " " + nested.getColumnNumber(), e);
                throw new XmlValidationException(nested.getMessage(), nested.getLineNumber(), nested.getColumnNumber());
            }
            logger.error("Problème avec la validation XML des hydrants : " + e.getMessage(), e);
            throw new BusinessException("Problème avec la désérialisation des hydrants : " + e.getMessage());
        } catch (JAXBException e) {
            SAXParseException nested = ExceptionUtils.getNestedExceptionWithClass(e, SAXParseException.class);
            if (nested != null) {
                logger.error("Problème avec la validation XML des hydrants : " + nested.getMessage() + nested.getLineNumber() + " " + nested.getColumnNumber(), e);
                throw new XmlValidationException(nested.getMessage(), nested.getLineNumber(), nested.getColumnNumber());
            }
            logger.error("Problème avec la désérialisation des hydrants : " + e.getMessage(), e);
            throw new BusinessException("Problème avec la désérialisation des hydrants : " + e.getMessage());
        } catch (XmlDroitException e) {
            logger.error(e.getMessage(), e);
            throw new XmlDroitException(e.getMessage());
        } catch (AnomalieException e) {
            logger.error(e.getMessage(), e);
            throw new AnomalieException(e.getMessage());
        } catch (Exception e) {
            if (e instanceof org.springframework.dao.EmptyResultDataAccessException) {
                throw new SQLBusinessException(e.getMessage(), "99");
            }
            if (e instanceof org.springframework.dao.DataIntegrityViolationException || e instanceof org.springframework.dao.DuplicateKeyException) {
                throw new SQLBusinessException(e.getMessage(), "23");
            }
            logger.error("Problème avec l'enregistrement des hydrants : " + e.getMessage(), e);
            throw new BusinessException("Problème avec  l'enregistrement des hydrants : " + e.getMessage());
        }
    }

    @Transactional
    public void deSerializeTournees(String xml, Integer version) throws BusinessException, XmlValidationException, SQLBusinessException{
        try {
            LstTournees tournees = (LstTournees) XmlUtil.unSerializeXml(xml, fr.sdis83.remocra.xml.LstTournees.class,"fr/sdis83/remocra/service/xml/Tournees.xsd");
            for (fr.sdis83.remocra.xml.Tournee tournee : tournees.getTournees()) {
                if(tournee.getId()!=null && Integer.valueOf(tournee.getPourcent()) != null){
                    Query qUpdate = entityManager.createQuery("UPDATE Tournee t set t.etat =:pourcentage where t.id = :id");
                    qUpdate.setParameter("pourcentage", tournee.getPourcent()).setParameter("id",tournee.getId());
                    qUpdate.executeUpdate();
                }
            }

            } catch (SAXException e) {
            SAXParseException nested = ExceptionUtils.getNestedExceptionWithClass(e, SAXParseException.class);
            if (nested != null) {
                logger.error("Problème avec la validation XML des tournées : " + nested.getMessage() + nested.getLineNumber() + " " + nested.getColumnNumber(), e);
                throw new XmlValidationException(nested.getMessage(), nested.getLineNumber(), nested.getColumnNumber());
            }
            logger.error("Problème avec la validation XML des tournées : " + e.getMessage(), e);
            throw new BusinessException("Problème avec la désérialisation des tournées : " + e.getMessage());
        } catch (JAXBException e) {
            SAXParseException nested = ExceptionUtils.getNestedExceptionWithClass(e, SAXParseException.class);
            if (nested != null) {
                logger.error("Problème avec la validation XML des tournées : " + nested.getMessage() + nested.getLineNumber() + " " + nested.getColumnNumber(), e);
                throw new XmlValidationException(nested.getMessage(), nested.getLineNumber(), nested.getColumnNumber());
            }
            logger.error("Problème avec la désérialisation des hydrants : " + e.getMessage(), e);
            throw new BusinessException("Problème avec la désérialisation des tournées : " + e.getMessage());
        }  catch (Exception e) {
            if (e instanceof org.springframework.dao.EmptyResultDataAccessException) {
                throw new SQLBusinessException(e.getMessage(), "99");
            }
            if (e instanceof org.springframework.dao.DataIntegrityViolationException || e instanceof org.springframework.dao.DuplicateKeyException) {
                throw new SQLBusinessException(e.getMessage(), "23");
            }
            logger.error("Problème avec l'enregistrement des tournées : " + e.getMessage(), e);
            throw new BusinessException("Problème avec  l'enregistrement des tournées : " + e.getMessage());
        }
    }


    /**
     * Mise à jour des hydrants.
     * 
     * Choix : si un élément n'est pas passé (NULL), on met à jour en NULL sauf
     * pour la photo, la nature, le diamètre et les débits/pressions
     * 
     * La date adéquate est définie en fonction du type de saisie et la date de
     * modification est maintenue.
     * 
     * Sécurité : Le territoire de compétence est vérifié. Les éléments de
     * l'onglet MCO et la photo sont sécurisés (si un élément est passé, on ne
     * bloque pas la remontée : on ne traite pas l'élément en question). Les
     * anomalies sont filtrées en fonction du type de saisie (Création,
     * Réception, Reconnaissance, Contrôle, Vérification), du profil de
     * l'utilisateur et de la configuration des anomalies. La création et la
     * réception sont possibles si l'utilisateur a le droit HYDRANTS.C
     * 
     * Rappel sur la portée des champs liée au droit HYDRANTS_MCO.C. champs
     * communs : anneeFabrication, codeDomaine, gestPointEau, courrier, photo ;
     * champs Pibi uniquement : codeMarque, codeModele, choc, gestReseau
     *
     * @param hydrant
     * @param hydrantXML
     * @throws IOException
     * @throws SecurityException
     * @throws FileUploadException
     * @throws BusinessException
     * @throws AnomalieException
     * @throws XmlDroitException
     */
    void updateHydrant(Hydrant hydrant, fr.sdis83.remocra.xml.Hydrant hydrantXML, Integer version)
            throws IOException, SecurityException, FileUploadException, BusinessException, AnomalieException, XmlDroitException {

        // Vérification du territoire de compétence de l'utilisateur connecté
        Boolean result = zoneCompetenceService.check(hydrant.getGeometrie(), utilisateurService.getCurrentZoneCompetenceId());
        if (!result) {
            if (hydrantXML.getNumero() == null || hydrantXML.getNumero().isEmpty()) {
                throw new XmlDroitException("Un des points d'eau à synchroniser est en dehors du territoire de compétence.");
            }
            throw new XmlDroitException("Le point d'eau " + hydrantXML.getNumero() + " est en dehors du territoire de compétence.");
        }

        Hydrant.TYPE_SAISIE typeSaisie = getTypeSaisie(hydrant, hydrantXML.isVerif());

        // Droits sur MCO
        boolean mcoCreate = authUtils.hasRight(TypeDroitEnum.HYDRANTS_MCO_C);

        hydrant.setAgent1(hydrantXML.getAgent1());
        hydrant.setAgent2(hydrantXML.getAgent2());
        hydrant.setComplement(hydrantXML.getComplement());

        // Dates
        hydrant.setDateModification(new Date());
        if (typeSaisie == Hydrant.TYPE_SAISIE.CREA) {
            // Toutes les dates à null (valeur par défaut)
        } else if (typeSaisie == Hydrant.TYPE_SAISIE.RECEP) {
            hydrant.setDateRecep(securedDate(hydrantXML.getDateRecep()));
        } else if (typeSaisie == Hydrant.TYPE_SAISIE.RECO) {
            hydrant.setDateReco(securedDate(hydrantXML.getDateReco()));
        } else if (typeSaisie == Hydrant.TYPE_SAISIE.CTRL) {
            hydrant.setDateContr(securedDate(hydrantXML.getDateContr()));
        } else if (typeSaisie == Hydrant.TYPE_SAISIE.VERIF) {
            hydrant.setDateVerif(securedDate(hydrantXML.getDateVerif()));
        }

        hydrant.setLieuDit(hydrantXML.getLieuDit());

        hydrant.setVoie(hydrantXML.getVoie());
        hydrant.setVoie2(hydrantXML.getVoie2());
        hydrant.setDispoTerrestre(getDispo(hydrantXML.getDispoTerrestre()));
        hydrant.setObservation(hydrantXML.getObservation());
        // La date d'attestation n'est jamais remontée par l'application mobile dans les versions antérieures à la 2. 
        // Il ne faut donc pas "vider" une date qui aurait été saisie par ailleurs.
        if (version != null && version > 1) {
            hydrant.setDateAttestation(hydrantXML.getDateAttestation());
        }

        // Eléments communs liés au droit MCO.C (anneeFabrication, codeDomaine,
        // gestPointEau, courrier)
        if (mcoCreate) {
            // Année. Si AnneeFabrication 0 : NULL
            if (hydrantXML.getAnneeFabrication() != null && hydrantXML.getAnneeFabrication().intValue() > 0) {
                hydrant.setAnneeFabrication(hydrantXML.getAnneeFabrication());
            } else {
                hydrant.setAnneeFabrication(null);
            }
            // Récupération du domaine
            if (hydrantXML.getCodeDomaine() != null && !hydrantXML.getCodeDomaine().isEmpty()) {
                hydrant.setDomaine(TypeHydrantDomaine.findTypeHydrantDomainesByCode(hydrantXML.getCodeDomaine()).getSingleResult());
            } else {
                hydrant.setDomaine(null);
            }
            hydrant.setGestPointEau(hydrantXML.getGestPointEau());
            hydrant.setCourrier(hydrantXML.getCourrier());
        }
        // Photo
        if (hydrantXML.getPhoto() != null && !hydrantXML.getPhoto().isEmpty()) {
            // si il y a une photo dans le xml, on ajoute/remplace
            // l'actuelle
            Document d;
            byte[] dataImage = Base64.decodeBase64(hydrantXML.getPhoto());
            InputStream in = new ByteArrayInputStream(dataImage);
            String filename = Hydrant.TITRE_PHOTO;
            BufferedImage bImageFromConvert = ImageIO.read(in);
            d = DocumentUtil.getInstance().createNonPersistedDocument(TypeDocument.HYDRANT, bImageFromConvert, filename, paramConfService.getDossierDocHydrant());
            HydrantDocument hd = new HydrantDocument();
            hd.setHydrant(hydrant);
            hd.setDocument(Hydrant.entityManager().merge(d));

            HydrantDocument toDetach = hydrant.getPhoto();
            if (toDetach != null) {
                // Suppression de l'ancienne photo
                hydrant.getHydrantDocuments().remove(toDetach);
            }
            // Ajout de la nouvelle photo
            hydrant.getHydrantDocuments().add(hd);
        }

        // Nature
        if (hydrantXML.getCodeNature() != null && !hydrantXML.getCodeNature().isEmpty()) {
            hydrant.setNature(TypeHydrantNature.findTypeHydrantNaturesByCode(hydrantXML.getCodeNature()).getSingleResult());
        }

        // Récupération de la commune
        if (hydrantXML.getCodeCommune() != null && !hydrantXML.getCodeCommune().isEmpty()) {
            hydrant.setCommune(Commune.findCommunesByCode(hydrantXML.getCodeCommune()).getSingleResult());
        } else {
            // La commune est obligatoire : on bloque
            throw new BusinessException("La commune est obligatoire");
        }

        //Nature deci
        if (hydrantXML.getCodeNatureDeci() != null && !hydrantXML.getCodeNatureDeci().isEmpty()) {
            hydrant.setNatureDeci(TypeHydrantNatureDeci.findTypeHydrantNatureDecisByCode(hydrantXML.getCodeNatureDeci()).getSingleResult());
        }

        // PIBI
        if (hydrantXML instanceof HydrantPibi) {
            HydrantPibi hydrantPibi = (HydrantPibi) hydrantXML;

            fr.sdis83.remocra.domain.remocra.HydrantPibi hydrantDomPibi = (fr.sdis83.remocra.domain.remocra.HydrantPibi) hydrant;

            // Diametre mise à jour si renseigné uniquement
            if (hydrantPibi.getCodeDiametre() != null && !hydrantPibi.getCodeDiametre().isEmpty()) {
                hydrantDomPibi.setDiametre(TypeHydrantDiametre.findTypeHydrantDiametresByCode(hydrantPibi.getCodeDiametre()).getSingleResult());
            }

            // Vérifications : données mises à jour si renseignées et positives
            if (hydrantPibi.getDebit() != null && hydrantPibi.getDebit().intValue() >= 0) {
                hydrantDomPibi.setDebit(hydrantPibi.getDebit());
            }
            if (hydrantPibi.getDebitMax() != null && hydrantPibi.getDebitMax().intValue() >= 0) {
                hydrantDomPibi.setDebitMax(hydrantPibi.getDebitMax());
            }
            if (hydrantPibi.getPression() != null && hydrantPibi.getPression().intValue() >= 0) {
                hydrantDomPibi.setPression(hydrantPibi.getPression());
            }
            if (hydrantPibi.getPressionDyn() != null && hydrantPibi.getPressionDyn().intValue() >= 0) {
                hydrantDomPibi.setPressionDyn(hydrantPibi.getPressionDyn());
            }
            if (hydrantPibi.getPressionDynDeb() != null && hydrantPibi.getPressionDynDeb().intValue() >= 0) {
                hydrantDomPibi.setPressionDynDeb(hydrantPibi.getPressionDynDeb());
            }

            // Eléments spécifiques aux PIBI liés au droit MCO.C (codeMarque,
            // codeModele, choc, gestReseau)
            if (mcoCreate) {
                if (hydrantPibi.getCodeMarque() != null && !hydrantPibi.getCodeMarque().isEmpty()) {
                    hydrantDomPibi.setMarque(TypeHydrantMarque.findTypeHydrantMarquesByCode(hydrantPibi.getCodeMarque()).getSingleResult());
                } else {
                    hydrantDomPibi.setMarque(null);
                }
                if (hydrantPibi.getCodeModele() != null && !hydrantPibi.getCodeModele().isEmpty()) {
                    hydrantDomPibi.setModele(TypeHydrantModele.findTypeHydrantModelesByCode(hydrantPibi.getCodeModele()).getSingleResult());
                } else {
                    hydrantDomPibi.setModele(null);
                }
                hydrantDomPibi.setRenversable(hydrantPibi.getRenversable());
                hydrantDomPibi.setGestReseau(hydrantPibi.getGestReseau());
            }

            // Pas de HBE pour les PIBI
            hydrant.setDispoHbe(null);
        }

        if (hydrantXML instanceof HydrantPena) {
            fr.sdis83.remocra.domain.remocra.HydrantPena hydrantDomPena = (fr.sdis83.remocra.domain.remocra.HydrantPena) hydrant;
            HydrantPena hydrantPena = (HydrantPena) hydrantXML;

            hydrantDomPena.setHbe(hydrantPena.isHbe());

            if (hydrantDomPena.getHbe()) {
                hydrantDomPena.setDispoHbe(getDispo(hydrantPena.getDispoHbe()));
            } else {
                hydrantDomPena.setDispoHbe(null);
            }

            // Coordonnées DFCI jamais modifiées sur la tablette
            // hydrantDomPena.setCoordDFCI(hydrantPena.getCoordDFCI());

            hydrantDomPena.setCapacite(hydrantPena.getCapacite());

            // PENA CITERNE ENTERRE /FIXE
            if (hydrantXML instanceof HydrantCiterneEnterre || hydrantXML instanceof HydrantCiterneFixe) {

                HydrantCiterneEnterre hydrantPenaCiterne = (HydrantCiterneEnterre) hydrantXML;

                hydrantDomPena.setQAppoint(hydrantPenaCiterne.getqAppoint());

                if (hydrantPenaCiterne.getCodeMateriau() != null && !hydrantPenaCiterne.getCodeMateriau().isEmpty()) {
                    hydrantDomPena.setMateriau(TypeHydrantMateriau.findTypeHydrantMateriausByCode(hydrantPenaCiterne.getCodeMateriau()).getSingleResult());
                } else {
                    ((fr.sdis83.remocra.domain.remocra.HydrantPena) hydrant).setMateriau(null);
                }

                // // Volumes constatés jamais modifiés sur la tablette
                // if (hydrantPenaCiterne.getCodeVolConstate() != null &&
                // !hydrantPenaCiterne.getCodeVolConstate().isEmpty()) {
                // hydrantDomPena.setVolConstate(TypeHydrantVolConstate.findTypeHydrantVolConstatesByCode(hydrantPenaCiterne.getCodeVolConstate()).getSingleResult());
                // } else {
                // hydrantDomPena.setVolConstate(null);
                // }
            }

            // PENA CITERNE FIXE
            if (hydrantXML instanceof HydrantCiterneFixe) {
                HydrantCiterneFixe hydrantPenaCiterneFixe = (HydrantCiterneFixe) hydrantXML;
                if (hydrantPenaCiterneFixe.getCodePositionnement() != null && !hydrantPenaCiterneFixe.getCodePositionnement().isEmpty()) {
                    hydrantDomPena
                            .setPositionnement(TypeHydrantPositionnement.findTypeHydrantPositionnementsByCode(hydrantPenaCiterneFixe.getCodePositionnement()).getSingleResult());
                } else {
                    hydrantDomPena.setPositionnement(null);
                }
            }
        }

        // On redéfinit le code, la zone spéciale éventuelle, le numéro interne
        // et le numéro
        NumeroUtil.setCodeZoneSpecAndNumeros(hydrant);

        // On attache l'organisme de l'utilisateur courant
        hydrant.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme());

        // Sauvegarde
        hydrant = hydrant.merge();

        // Anomalies
        deleteAnomalieByHydrantNatureSaisie(hydrant.getId().intValue(), hydrant.getNature().getCode(), typeSaisie);

        if (hydrantXML.getAnomalies().getAnomalies() != null) {
            ArrayList<Anomalie> lstAnomaliesXML = hydrantXML.getAnomalies().getAnomalies();
            boolean isHbe = hydrantXML instanceof HydrantPena ? ((HydrantPena) hydrantXML).isHbe() : false;

           // if (checkAnomalies(typeSaisie, lstAnomaliesXML, hydrant.getNature().getCode(), isHbe)) {
                insertAnomalies(lstAnomaliesXML, hydrant.getId().intValue());
           /* } else {
                throw new AnomalieException("Les anomalies remontées ne sont pas en adéquation avec le référentiel");
            }*/
        }

        if (hydrantXML instanceof HydrantPibi) {
            // Pour déclencher le calcul des anomalies via trigger
            entityManager.createNativeQuery("update remocra.hydrant_pibi set debit=debit where id=:id")
                    .setParameter("id", hydrant.getId())
                    .executeUpdate();
        }
    }

    public Hydrant.Disponibilite getDispo(String dispo) {
        try {
            return Hydrant.Disponibilite.valueOf(dispo);
        } catch (IllegalArgumentException ex) {
            logger.error("Disponibilité inconnue : " + dispo);
            return null;
        }
    }

    protected void serializeXmlExceptionManaged(Class<?> classe, Object lst, String referentiel, OutputStream out) throws SQLBusinessException, BusinessException {
        try {
            XmlUtil.serializeXml(classe, lst, referentiel, out);
        } catch (Exception e) {
            logger.error("Erreur de parsing xml", e);
            GenericJDBCException nested = ExceptionUtils.getNestedExceptionWithClass(e, GenericJDBCException.class);
            if (nested != null) {
                throw new SQLBusinessException(nested.getMessage(), nested.getSQLState());
            }

            throw new BusinessException("Problème pour la récupération des données");
        }
    }

    /**
     * @param hydrant
     * @param isVerif
     * @return
     * @throws BusinessException
     * @throws XmlDroitException
     */
    private Hydrant.TYPE_SAISIE getTypeSaisie(Hydrant hydrant, boolean isVerif) throws BusinessException, XmlDroitException {
        if (hydrant.getId() == null) {
            if (!authUtils.hasRight(TypeDroitEnum.HYDRANTS_C)) {
                throw new XmlDroitException("L'utilisateur n'a pas les droits suffisants pour la remontée des anomalies");
            }
            return Hydrant.TYPE_SAISIE.CREA;
        } else if (hydrant.getId() != null && hydrant.getDateRecep() == null) {
            if (!authUtils.hasRight(TypeDroitEnum.HYDRANTS_RECEPTION_C)) {
                throw new XmlDroitException("L'utilisateur n'a pas les droits suffisants pour la remontée des anomalies");
            }
            return Hydrant.TYPE_SAISIE.RECEP;
        } else if (authUtils.hasRight(TypeDroitEnum.HYDRANTS_CONTROLE_C)) {
            return isVerif ? Hydrant.TYPE_SAISIE.VERIF : Hydrant.TYPE_SAISIE.CTRL;
        } else if (authUtils.hasRight(TypeDroitEnum.HYDRANTS_RECONNAISSANCE_C)) {
            return Hydrant.TYPE_SAISIE.RECO;
        }

        throw new XmlDroitException("L'utilisateur n'a pas les droits suffisants pour la remontée des anomalies");
    }

    /**
     * Permet de vérifier que les anomalies remontées, pour la nature de
     * l'hydrant, le type de saisie et l'hbe donnés, existent dans le
     * référentiel. Le resultat du count(*) doit être égal à la taille de la
     * liste des anomalies (on suppose qu'il n'y a pas de doublons dans la
     * remontée des anomalies)
     * 
     * @param typeSaisie
     * @param lstAnomaliesXML
     * @param codeNature
     * @param isHbe
     * @return
     * @throws BusinessException
     */
    private boolean checkAnomalies(Hydrant.TYPE_SAISIE typeSaisie, ArrayList<Anomalie> lstAnomaliesXML, String codeNature, boolean isHbe) throws BusinessException {

        ArrayList<String> lstAnomalies = new ArrayList<String>();

        for (Anomalie anomalie : lstAnomaliesXML) {
            lstAnomalies.add("'" + anomalie.getCode() + "'");
        }

        String param_anomalies = lstAnomalies.toString().replace('[', ' ').replace(']', ' ');

        Query query = entityManager
                .createNativeQuery("SELECT count(*) from remocra.type_hydrant_anomalie tha join remocra.type_hydrant_anomalie_nature than on (tha.id = than.anomalie) "
                        + "join remocra.type_hydrant_nature thn on (than.nature = thn.id) "
                        + "join remocra.type_hydrant_anomalie_nature_saisies thans on (than.id  = thans.type_hydrant_anomalie_nature) "
                        + "join remocra.type_hydrant_saisie ths on (thans.saisies = ths.id)" + "where thn.code = :codeNature and tha.code in (" + param_anomalies + ") "
                        + "and ths.code = :typeSaisie and case when :isHbe then than.val_indispo_hbe >= 0  OR than.val_indispo_terrestre >= 0 else than.val_indispo_terrestre >= 0 end ")
                .setParameter("codeNature", codeNature).setParameter("typeSaisie", typeSaisie.toString()).setParameter("isHbe", isHbe);

        BigInteger nbAnomalieReturn = (BigInteger) query.getSingleResult();
        if (nbAnomalieReturn.intValue() == lstAnomaliesXML.size()) {
            return true;
        }

        return false;
    }

    private void deleteAnomalieByHydrantNatureSaisie(int hydrantId, String codeNature, Hydrant.TYPE_SAISIE typeSaisie) {
        // Récupération des anomalies pour l'hydrant, nature et type saisie
        String subRequest = "select distinct thya.id as anomalie_id " + "from remocra.hydrant hy " + "join remocra.hydrant_anomalies hya on (hy.id = hya.hydrant) "
                + "join remocra.type_hydrant_anomalie thya on(thya.id =hya.anomalies) join remocra.type_hydrant_anomalie_nature thyan on (thya.id = thyan.anomalie) "
                + "join remocra.type_hydrant_nature thyn on (thyan.nature = thyn.id) "
                + "join remocra.type_hydrant_anomalie_nature_saisies thyans on (thyan.id = thyans.type_hydrant_anomalie_nature) "
                + "join remocra.type_hydrant_saisie s on ( s.id = thyans.saisies)  where hy.id = :hydrant and thyn.code = :nature and s.code = :saisie ";

        Query query = entityManager.createNativeQuery("DELETE FROM remocra.hydrant_anomalies where hydrant =  :hydrant and anomalies in (" + subRequest + ")")
                .setParameter("hydrant", hydrantId).setParameter("nature", codeNature).setParameter("saisie", typeSaisie.toString());
        query.executeUpdate();
    }

    private void insertAnomalies(ArrayList<Anomalie> anomalies, int hydrantId) {
        Query query;
        for (Anomalie anomalie : anomalies) {
            query = entityManager
                    .createNativeQuery(
                            "insert into remocra.hydrant_anomalies(hydrant,anomalies) select :hydrantId, (select  id from remocra.type_hydrant_anomalie where code = :typeHydrantAnomalie)")
                    .setParameter("hydrantId", hydrantId).setParameter("typeHydrantAnomalie", anomalie.getCode());
            query.executeUpdate();
        }
    }

    /**
     * Retourne la date passée ou la date du jour si celle qui est passée est
     * trop passée (dépassé par le commentaire présent ? Humm).
     * 
     * Une date plus ancienne que deux ans est considérée comme mauvaise.
     * 
     * @param date
     * @return
     */
    private static Date securedDate(Date date) {
        if (date == null) {
            return new Date();
        }
        Calendar calDate = new GregorianCalendar();
        calDate.setTime(date);
        Calendar calToday = new GregorianCalendar();
        calToday.setTime(new Date());

        if (calDate.get(Calendar.YEAR) < calToday.get(Calendar.YEAR) - 1) {
            return new Date();
        }
        return date;
    }
}
