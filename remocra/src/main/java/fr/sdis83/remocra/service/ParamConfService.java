package fr.sdis83.remocra.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import fr.sdis83.remocra.domain.remocra.ParamConf.ParamConfParam;

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
            log.warn("Paramètre " + cle + " non présent (EmptyResultDataAccessException), restitution de la valeur par défaut");
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

    public String getDossierDepotRci() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_RCI);
    }

    public String getDossierDocHydrant() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DOC_HYDRANT);
    }

    public String getDossierDepotBloc() {
        return (String) this.getValue(ParamConfParam.DOSSIER_DEPOT_BLOC);
    }

    public Integer getIdTraitementAtlas() {
        return (Integer) this.getValue(ParamConfParam.ID_TRAITEMENT_ATLAS);
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

    public Integer getToleranceChargementMetres() {
        return (Integer) this.getValue(ParamConfParam.PERMIS_TOLERANCE_CHARGEMENT_METRES, 1000);
    }

    public Integer getToleranceVoiesMetres() {
        return (Integer) this.getValue(ParamConfParam.PERMIS_TOLERANCE_VOIES_METRES, 150);
    }

    public String getMessageEntete() {
        return (String) this.getValue(ParamConfParam.MESSAGE_ENTETE);
    }

    public Long getSystemUtilisateurId() {
        return (Long) this.getValue(ParamConfParam.PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID);
    }

    public Integer getHydrantRenouvellementReco() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO);
    }

    public Integer getHydrantRenouvellementCtrl() {
        return (Integer) this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL);
    }

    public String getEmailCreationRci() {
        return (String) this.getValue(ParamConfParam.EMAIL_DEST_CREATION_RCI);
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
        data.put("delai_rnvl_reco", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO));
        data.put("delai_ctrl_warn", this.getValue(ParamConfParam.HYDRANT_DELAI_CTRL_WARN));
        data.put("delai_ctrl_urgent", this.getValue(ParamConfParam.HYDRANT_DELAI_CTRL_URGENT));
        data.put("delai_rnvl_ctrl", this.getValue(ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL));

        return serializer.serialize(data);
    }

}
