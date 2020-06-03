package fr.sdis83.remocra.service;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import fr.sdis83.remocra.domain.remocra.ParamConf.ParamConfParam;
import fr.sdis83.remocra.domain.utils.Password;
import fr.sdis83.remocra.util.NumeroUtil;
import fr.sdis83.remocra.util.NumeroUtil.MethodeNumerotation;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.persistence.NoResultException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Configuration
public class ParamConfService {

    private final Logger log = Logger.getLogger(getClass());

    protected Object getValue(ParamConfParam pcp, Object defaultValue) {
        Object result = getValue(pcp);
        return result == null ? defaultValue : result;
    }

    protected Object getValue(String cle, Object defaultValue) {
        Object result = getValue(cle);
        return result == null ? defaultValue : result;
    }

    @SuppressWarnings("all")
    protected Object getValue(ParamConfParam pcp) {
        return getValue(pcp.getCle());
    }

    @SuppressWarnings("all")
    protected Object getValue(String cle) {
        try {
            ParamConf pc = ParamConf.findParamConfsByCleEquals(cle).getSingleResult();
            Class cl = ParamConfParam.getClassFromCle(cle);
            Constructor constr = cl.getConstructor(String.class);
            return constr.newInstance(pc.getValeur());
        } catch (NoResultException e) {
            log.warn("Paramètre " + cle + " non présent (NoResultException), restitution de la valeur par défaut");
        } catch (EmptyResultDataAccessException e) {
            log.warn("Paramètre " + cle
                    + " non présent (EmptyResultDataAccessException), restitution de la valeur par défaut");
        } catch (Exception e) {
            log.error("Paramètre " + cle + ", restitution de la valeur par défaut", e);
        }
        return null;
    }

    public String getUrlSite() {
        return (String) this.getValue(ParamConfParam.PDI_URL_SITE);
    }

    public Integer getToleranceAssociationCiternePIMetres() {
        return (Integer) this.getValue(ParamConfParam.CITERNE_TOLERANCE_ASSOCIATION_PI_METRES, 500);
    }

    public Integer getCriseNouvelEvtDelaiMinutes() {
        return (Integer) this.getValue(ParamConfParam.CRISE_NOUVEL_EVT_DELAI_MINUTES, 20);
    }

    public String getClesIgn() {
        return (String) this.getValue(ParamConfParam.CLES_IGN, "4n507j21zeha5rp5pkll48vj");
    }

    public String getDossierRessourcesExternes() {
        return (String) this.getValue(ParamConfParam.DOSSIER_RESSOURCES_EXTERNES);
    }

    public String getDossierDepotDelib() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_DELIB);
    }

    public String getDossierDepotDeclaHydrant() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_DECLA_HYDRANT);
    }

    public String getDossierDepotRecepTravaux() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_RECEP_TRAVAUX);
    }

    public String getDossierDepotPdi() {
        return (String) this.getValue(ParamConfParam.PDI_DOSSIER_DEPOT);
    }

    public String getDossierDepotAlerte() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_ALERTE);
    }

    public String getDossierDepotPermis() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_PERMIS);
    }

    public String getDossierGetFeatureInfo() {
        return (String) this.getValue(ParamConfParam.DOSSIER_GETFEATUREINFO);
    }

    public String getDossierLayers() {
        return (String) this.getValue(ParamConfParam.DOSSIER_LAYERS);
    }

    public String getDossierDepotRci() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_RCI);
    }

    public String getDossierDocHydrant() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DOC_HYDRANT);
    }

    public String getDossierCourriersExternes() {return (String) this.getValue(ParamConfParam.DOSSIER_COURRIER);}

    public String getDossierDocOldebVisite() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DOC_OLDEBVISITE);
    }

    public String getDossierDocCrise() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DOC_CRISE);
    }

    public String getDossierDepotBloc() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_BLOC);
    }

    public String getDossierDepotAttestation() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_ATTESTATION);
    }

    public String getDossierDepotPlanification() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_PLANIFICATION);
    }

    public Integer getIdTraitementAtlas() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_ATLAS);
    }

    public Integer getIdTraitementOldeb() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_OLDEB);
    }
    public Integer getIdTraitementRequetage() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_REQUETAGE);
    }

    public Integer getIdTraitementPurgeKml() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_PURGE_KML);
    }

    public String getPdiCheminKml() {
        return (String) this.getValue(ParamConfParam.PDI_CHEMIN_KML);
    }

    public String getPdiCheminLog() {
        return (String) this.getValue(ParamConfParam.PDI_CHEMIN_LOG);
    }

    public String getPdiCheminPfxFile(){
        return (String) this.getValue(ParamConfParam.PDI_PDF_SIGN_PFX_FILE);
    }

    public String getPdiPfxPassword(){
        return (String)(this.getValue(ParamConfParam.PDI_PDF_SIGN_PFX_PASSWORD)).toString();
    }

    public Integer getToleranceChargementMetres() {
        return (Integer) this.getValue(ParamConfParam.PERMIS_TOLERANCE_CHARGEMENT_METRES, 1000);
    }

    public Integer getToleranceVoiesMetres() {
        return (Integer) this.getValue(ParamConfParam.TOLERANCE_VOIES_METRES, 150);
    }

    public String getMessageEntete() {
        return (String) this.getValue(ParamConfParam.MESSAGE_ENTETE);
    }

    protected static String TITRE_PAGE_DEFAULT_VALUE = "SDIS - REMOcRA";
    public String getTitrePage() {
        String value = (String) this.getValue(ParamConfParam.TITRE_PAGE, TITRE_PAGE_DEFAULT_VALUE);
        return "".equals(value) ? TITRE_PAGE_DEFAULT_VALUE : value;
    }

    public String getMentionCnil() {
        return (String) this.getValue(ParamConfParam.MENTION_CNIL, "");
    }

    public Long getSystemUtilisateurId() {
        return (Long) this.getValue(ParamConfParam.PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID);
    }

    public Integer getHydrantRenouvellementRecoPublic() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PUBLIC, 180);
    }

    public Integer getHydrantRenouvellementRecoPrive() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PRIVE, 365);
    }

    public Integer getHydrantNombreHistorique() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_NOMBRE_HISTORIQUE,3);
    }

    public Integer getHydrantRenouvellementCtrlPublic() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC, 365);
    }

    public Integer getHydrantRenouvellementCtrlPrive() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PRIVE, 365);
    }

    public Boolean getHydrantVisiteRapide() {
        return (Boolean) this.getValue(ParamConfParam.HYDRANT_VISITE_RAPIDE, false);
    }

    public Integer getHydrantHighlightDuree() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_HIGHLIGHT_DUREE, 1000);
    }

    public Integer getHydrantLongueIndisponibiliteJours() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_JOURS);
    }

    public String getHydrantLongueIndisponibiliteMessage() {
        return (String) this.getValue(ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_MESSAGE);
    }

    public String getHydrantLongueIndisponibiliteTypeOrganisme() {
        return (String) this.getValue(ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_TYPEORGANISME, "^(COMMUNE|EPCI)$");
    }

    public Integer getVitesseEau() {
        return (Integer) this.getValue(ParamConfParam.VITESSE_EAU, 2);
    }

    public static String HYDRANT_COLONNES_DEFAULT[] = {"numero", "nomTournee", "natureNom", "dateReco", "dateContr", "dispoTerrestre", "dispoHbe"};
    public String[] getHydrantColonnes(){
        String hydrantColonnes = (String) this.getValue(ParamConfParam.HYDRANT_COLONNES);
        if(hydrantColonnes == null || hydrantColonnes.length() ==0){
            return HYDRANT_COLONNES_DEFAULT;
        }
        return hydrantColonnes.split("%");
    }

    public Boolean getHydrantZoomNumero() {
        return (Boolean) this.getValue(ParamConfParam.HYDRANT_ZOOM_NUMERO, false);
    }

    public MethodeNumerotation getHydrantNumerotationMethode() {
        return NumeroUtil.getHydrantNumerotationMethode();
    }

    public String getEmailCreationRci() {
        return (String) this.getValue(ParamConfParam.EMAIL_DEST_CREATION_RCI);
    }

    public Boolean getHydrantRenumerotationActivation() {
        return (Boolean) this.getValue(ParamConfParam.HYDRANT_RENUMEROTATION_ACTIVATION, true);
    }


    public String getEmailDepotDelib() {
        return (String) this.getValue(ParamConfParam.EMAIL_DEST_DEPOT_DELIB);
    }

    public String getEmailDepotDeclaHydrant() {
        return (String) this.getValue(ParamConfParam.EMAIL_DEST_DEPOT_DECLAHYDRANT);
    }

    public String getEmailDepotRecepTravaux() {
        return (String) this.getValue(ParamConfParam.EMAIL_DEST_DEPOT_RECEPTRAVAUX);
    }

    public Integer getIdTraitementHydrantsNonNum() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_HYDRANTS_NON_NUM);
    }

    public Integer getIdTraitementNbAlertesParUtilisateur() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR);
    }

    public String getDefaultOrientationX() {
        return (String) this.getValue(ParamConfParam.DEFAULT_ORIENTATION_X, "E");
    }

    public String getDefaultOrientationY() {
        return (String) this.getValue(ParamConfParam.DEFAULT_ORIENTATION_Y, "N");
    }

    /**
     * Par exemple, un tableau avec remocra:ADMINISTRATIF et remocra:RISQUE
     * (initialement séparés par un %)
     *
     * @return
     */
    public String[] getWmsPublicLayers() {
        return ((String) this.getValue(ParamConfParam.WMS_PUBLIC_LAYERS, "")).split("%");
    }

    public String getWmsBaseUrl() {
        return (String) this.getValue(ParamConfParam.WMS_BASE_URL, "http://localhost:8080/geoserver");
    }

    public String getCoordonneesFormatAffichage() {
        return (String) this.getValue(ParamConfParam.COORDONNEES_FORMAT_AFFICHAGE, "DD_DDDD");
    }

    public String getComplexitePassword() {
        return (String) this.getValue(ParamConfParam.COMPLEXITE_PASSWORD, "libre");
    }

    @Transactional
    public ParamConf update(ParamConf record) {
        log.info("updateParamConf : " + record.getCle());

        ParamConf attached = ParamConf.findParamConf(record.getCle());
        attached.setValeur(record.getValeur());
        return attached.merge();
    }

    @Transactional
    public List<ParamConf> update(Collection<ParamConf> records) {
        List<ParamConf> returned = new ArrayList<ParamConf>(records.size());
        for (ParamConf record : records) {
            returned.add(update(record));
        }
        return returned;
    }

    public Object getHydrantCfg() {
        JSONSerializer serializer = new JSONSerializer();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("delai_reco_warn", this.getValue(ParamConfParam.HYDRANT_DELAI_RECO_WARN));
        data.put("delai_reco_urgent", this.getValue(ParamConfParam.HYDRANT_DELAI_RECO_URGENT));
        data.put("delai_rnvl_reco_prive", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PRIVE));
        data.put("delai_rnvl_reco_public", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PUBLIC));
        data.put("delai_ctrl_warn", this.getValue(ParamConfParam.HYDRANT_DELAI_CTRL_WARN));
        data.put("delai_ctrl_urgent", this.getValue(ParamConfParam.HYDRANT_DELAI_CTRL_URGENT));
        data.put("delai_rnvl_ctrl_prive", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PRIVE));
        data.put("delai_rnvl_ctrl_public", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC));

        return serializer.serialize(data);
    }

    public String getHydrantSymbologieMethode() {
        return (String) this.getValue(ParamConfParam.HYDRANT_SYMBOLOGIE_METHODE, "83");
    }

    // Sortie JWT
    public String getJwtOutPublicKey() {
        return (String) this.getValue(ParamConfParam.SORTIE_JWT_CLEPUBLIQUE);
    }

    public String getJwtOutPrivateKey() {
        return (String) this.getValue(ParamConfParam.SORTIE_JWT_CLEPRIVEE);
    }

    public String getJwtOutIssuer() {
        return (String) this.getValue(ParamConfParam.SORTIE_JWT_ISSUER, "remocra");
    }

    public Integer getJwtOutValidite() {
        return (Integer) this.getValue(ParamConfParam.SORTIE_JWT_VALIDITE_SEC, 30);
    }

    // LDAP

    public static enum LdapMethod {
        NONE, SIMPLE, SEARCHUSER
    }

    public LdapMethod getLdapMethod() {
        String host = getLdapUrlHost();
        if (host == null || host.isEmpty()) {
            return LdapMethod.NONE;
        }

        String userFilter = getLdapUserFilter();
        if (userFilter == null || userFilter.isEmpty()) {
            // Authentification LDAP basée sur couple username/password
            // Possible si dn utilisatetur = identifiant remocra
            return LdapMethod.SIMPLE;
        }

        // Etape de recherche de l'utilisateur pour obtenir son DN
        String dn = getLdapAdminDn();
        String password = getLdapAdminPassword();
        String userBaseName = getLdapUserBaseName();
        if (dn == null || dn.isEmpty() || password == null || password.isEmpty() || userBaseName == null || userBaseName.isEmpty()) {
            // Il manque les informations de connexion administrateur
            return LdapMethod.NONE;
        }
        return LdapMethod.SEARCHUSER;
    }

    public String getLdapUrlHost() {
        return (String) this.getValue(ParamConfParam.PDI_LDAP_URL_HOST);
    }

    public Integer getLdapUrlPort() {
        return (Integer) this.getValue(ParamConfParam.PDI_LDAP_URL_PORT, 389);
    }

    public String getLdapUrlBaseDn() {
        return (String) this.getValue(ParamConfParam.PDI_LDAP_URL_BASE_DN);
    }

    public String getLdapAdminDn() {
        return (String) this.getValue(ParamConfParam.PDI_LDAP_ADMIN_DN);
    }

    public String getLdapAdminPassword() {
        Password p = (Password) this.getValue(ParamConfParam.PDI_LDAP_ADMIN_PASSWORD);
        return p == null ? null : p.toString();
    }

    public String getLdapUserBaseName() {
        return (String) this.getValue(ParamConfParam.PDI_LDAP_USER_BASE_NAME, "(&(objectclass=user)(sAMAccountName=[USERNAME]))");
    }

    public String getLdapUserFilter() {
        return (String) this.getValue(ParamConfParam.PDI_LDAP_USER_FILTER);
    }

    public String getLdapUrl() {
        String baseDn = getLdapUrlBaseDn();
        return "ldap://" + getLdapUrlHost() + ":" + getLdapUrlPort() + (baseDn == null || baseDn.isEmpty() ? "" : "/" + baseDn);
    }

    public Hashtable<String, String> getLdapEnvironmentSimple() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getLdapUrl());
        // To get rid of the PartialResultException when using Active Directory
        env.put(Context.REFERRAL, "follow");
        // Needed for the Bind (User Authorized to Query the LDAP server)
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        return env;
    }

    public Hashtable<String, String> getLdapEnvironmentSearchUser() {
        Hashtable<String, String> env = getLdapEnvironmentSimple();
        env.put(Context.SECURITY_PRINCIPAL, getLdapAdminDn());
        env.put(Context.SECURITY_CREDENTIALS, getLdapAdminPassword());
        return env;
    }

}
