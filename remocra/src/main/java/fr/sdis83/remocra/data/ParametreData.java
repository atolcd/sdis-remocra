package fr.sdis83.remocra.data;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.enums.TypeParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import fr.sdis83.remocra.domain.remocra.ParamConf;
import fr.sdis83.remocra.domain.utils.Password;
import fr.sdis83.remocra.util.NumeroUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;

public class ParametreData {
  private final Map<String, CleValeurClasseData> mapValueByKey = new HashMap<>();

  public ParametreData(
      Collection<CleValeurClasseData> listParamConfData, Collection<Parametre> listParametreData) {
    for (CleValeurClasseData cleValeurClasseData : listParamConfData) {
      cleValeurClasseData.setClazz(
          ParamConf.ParamConfParam.getClassFromCle(cleValeurClasseData.getCle()));
      mapValueByKey.put(cleValeurClasseData.getCle(), cleValeurClasseData);
    }
    for (Parametre parametre : listParametreData) {
      mapValueByKey.put(
          parametre.getCleParametre(),
          new CleValeurClasseData(
              parametre.getCleParametre(),
              parametre.getValeurParametre(),
              getClassFromTypeParametre(parametre.getTypeParametre())));
    }
  }

  /**
   * Retourne la classe attendue pour un type de paramètre donné
   *
   * @param typeParametre {@link TypeParametre}
   * @return Class<?>
   */
  private static Class<?> getClassFromTypeParametre(TypeParametre typeParametre) {
    switch (typeParametre) {
      case INTEGER:
        return Integer.class;
      case DOUBLE:
        return Double.class;
      case BOOLEAN:
        return Boolean.class;
      case GEOMETRY:
      case BINARY:
      case STRING:
      default:
        return String.class;
    }
  }

  public String getValeurString(String cle) {
    if (!mapValueByKey.containsKey(cle)) {
      return null;
    }
    return mapValueByKey.get(cle).getValeur().toString();
  }

  public Object getValeur(String cle) {
    if (!mapValueByKey.containsKey(cle)) {
      return null;
    }
    if (mapValueByKey.get(cle).getClazz().equals(Integer.class)) {
      return Integer.valueOf((String) mapValueByKey.get(cle).getValeur());
    }
    if (mapValueByKey.get(cle).getClazz().equals(Long.class)) {
      return Long.valueOf((String) mapValueByKey.get(cle).getValeur());
    }
    if (mapValueByKey.get(cle).getClazz().equals(Boolean.class)) {
      return Boolean.valueOf((String) mapValueByKey.get(cle).getValeur());
    }
    if (mapValueByKey.get(cle).getClazz().equals(Password.class)) {
      return new Password((String) mapValueByKey.get(cle).getValeur());
    }

    return mapValueByKey.get(cle).getValeur().toString();
  }

  public Class<?> getClazz(String cle) {
    if (!mapValueByKey.containsKey(cle)) {
      // Arbitraire, mais on ne veut pas retourner NULL
      return String.class;
    }
    return mapValueByKey.get(cle).getClazz();
  }

  //  *******************************************************
  // Facilitateurs
  // *******************************************************

  public String getUrlSite() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_URL_SITE.getCle());
  }

  public Boolean getAffichageIndispo() {
    return (Boolean) this.getValeur(GlobalConstants.AFFICHAGE_INDISPO);
  }

  public String getPasswordAdmin() {
    return (String) this.getValeur(GlobalConstants.MDP_ADMINISTRATEUR);
  }

  public Integer getToleranceAssociationCiternePIMetres() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.CITERNE_TOLERANCE_ASSOCIATION_PI_METRES.getCle());
  }

  public Integer getCriseNouvelEvtDelaiMinutes() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.CRISE_NOUVEL_EVT_DELAI_MINUTES.getCle());
  }

  public String getClesIgn() {
    return (String) this.getValeur(ParamConf.ParamConfParam.CLES_IGN.getCle());
  }

  public String getDossierRessourcesExternes() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_RESSOURCES_EXTERNES.getCle());
  }

  public String getDossierDepotDelib() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_DELIB.getCle());
  }

  public String getDossierDepotDeclaHydrant() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_DECLA_HYDRANT.getCle());
  }

  public String getDossierDepotRecepTravaux() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_RECEP_TRAVAUX.getCle());
  }

  public String getDossierDepotPdi() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_DOSSIER_DEPOT.getCle());
  }

  public String getDossierDepotAlerte() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_ALERTE.getCle());
  }

  public String getDossierDepotPermis() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_PERMIS.getCle());
  }

  public String getDossierGetFeatureInfo() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_GETFEATUREINFO.getCle());
  }

  public String getDossierLayers() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_LAYERS.getCle());
  }

  public String getDossierDepotRci() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_RCI.getCle());
  }

  public String getDossierDocHydrant() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DOC_HYDRANT.getCle());
  }

  public String getDossierCourriersExternes() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_COURRIER.getCle());
  }

  public String getDossierDocOldebVisite() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DOC_OLDEBVISITE.getCle());
  }

  public String getDossierDocCrise() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DOC_CRISE.getCle());
  }

  public String getDossierDepotBloc() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_BLOC.getCle());
  }

  public String getDossierDepotAttestation() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_ATTESTATION.getCle());
  }

  public String getDossierDepotPlanification() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DOSSIER_DEPOT_PLANIFICATION.getCle());
  }

  public Integer getIdTraitementAtlas() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_ATLAS.getCle());
  }

  public Integer getIdTraitementOldeb() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_OLDEB.getCle());
  }

  public Integer getIdTraitementRequetage() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_REQUETAGE.getCle());
  }

  public Integer getIdTraitementPurgeKml() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_PURGE_KML.getCle());
  }

  public String getPdiCheminKml() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_CHEMIN_KML.getCle());
  }

  public String getPdiCheminLog() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_CHEMIN_LOG.getCle());
  }

  public String getPdiCheminPfxFile() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_PDF_SIGN_PFX_FILE.getCle());
  }

  public String getPdiPfxPassword() {
    return (String)
        (this.getValeur(ParamConf.ParamConfParam.PDI_PDF_SIGN_PFX_PASSWORD.getCle())).toString();
  }

  public Integer getToleranceChargementMetres() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.PERMIS_TOLERANCE_CHARGEMENT_METRES.getCle());
  }

  public Integer getToleranceVoiesMetres() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.TOLERANCE_VOIES_METRES.getCle());
  }

  public String getMessageEntete() {
    return (String) this.getValeur(ParamConf.ParamConfParam.MESSAGE_ENTETE.getCle());
  }

  public String getTitrePage() {
    return (String) this.getValeur(ParamConf.ParamConfParam.TITRE_PAGE.getCle());
  }

  public String getMentionCnil() {
    return (String) this.getValeur(ParamConf.ParamConfParam.MENTION_CNIL.getCle());
  }

  public Long getSystemUtilisateurId() {
    return (Long)
        this.getValeur(ParamConf.ParamConfParam.PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID.getCle());
  }

  public Integer getHydrantRenouvellementRecoPublic() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PUBLIC.getCle());
  }

  public Integer getHydrantRenouvellementRecoPrive() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PRIVE.getCle());
  }

  public Integer getHydrantNombreHistorique() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.HYDRANT_NOMBRE_HISTORIQUE.getCle());
  }

  public Integer getHydrantRenouvellementCtrlPublic() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC.getCle());
  }

  public Integer getHydrantRenouvellementCtrlPrive() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PRIVE.getCle());
  }

  public Boolean getHydrantVisiteRapide() {
    return (Boolean) this.getValeur(ParamConf.ParamConfParam.HYDRANT_VISITE_RAPIDE.getCle());
  }

  public Integer getHydrantHighlightDuree() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.HYDRANT_HIGHLIGHT_DUREE.getCle());
  }

  public Integer getHydrantLongueIndisponibiliteJours() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_JOURS.getCle());
  }

  public String getHydrantLongueIndisponibiliteMessage() {
    return (String)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_MESSAGE.getCle());
  }

  public String getHydrantLongueIndisponibiliteTypeOrganisme() {
    return (String)
        this.getValeur(
            ParamConf.ParamConfParam.HYDRANT_LONGUE_INDISPONIBILITE_TYPEORGANISME.getCle());
  }

  public Integer getVitesseEau() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.VITESSE_EAU.getCle());
  }

  public Boolean getHydrantZoomNumero() {
    return (Boolean) this.getValeur(ParamConf.ParamConfParam.HYDRANT_ZOOM_NUMERO.getCle());
  }

  public Integer getHydrantDeplacementDistWarn() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_DEPLACEMENT_DIST_WARN.getCle());
  }

  public NumeroUtil.MethodeNumerotation getHydrantNumerotationMethode() {
    return NumeroUtil.getHydrantNumerotationMethode();
  }

  public String getEmailCreationRci() {
    return (String) this.getValeur(ParamConf.ParamConfParam.EMAIL_DEST_CREATION_RCI.getCle());
  }

  public Boolean getHydrantRenumerotationActivation() {
    return (Boolean)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENUMEROTATION_ACTIVATION.getCle());
  }

  public Boolean getHydrantGenerationCarteTournee() {
    return (Boolean)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_GENERATION_CARTE_TOURNEE.getCle());
  }

  public Boolean getHydrantMethodeTriAlphanumerique() {
    return (Boolean)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_METHODE_TRI_ALPHANUMERIQUE.getCle());
  }

  public Long getHydrantToleranceCommuneMetres() {
    return (Long)
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_TOLERANCE_COMMUNE_METRES.getCle());
  }

  public String getEmailDepotDelib() {
    return (String) this.getValeur(ParamConf.ParamConfParam.EMAIL_DEST_DEPOT_DELIB.getCle());
  }

  public String getEmailDepotDeclaHydrant() {
    return (String) this.getValeur(ParamConf.ParamConfParam.EMAIL_DEST_DEPOT_DECLAHYDRANT.getCle());
  }

  public String getEmailDepotRecepTravaux() {
    return (String) this.getValeur(ParamConf.ParamConfParam.EMAIL_DEST_DEPOT_RECEPTRAVAUX.getCle());
  }

  public Integer getIdTraitementHydrantsNonNum() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_HYDRANTS_NON_NUM.getCle());
  }

  public Integer getIdTraitementNbAlertesParUtilisateur() {
    return (Integer)
        this.getValeur(ParamConf.ParamConfParam.ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR.getCle());
  }

  public String getDefaultOrientationX() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DEFAULT_ORIENTATION_X.getCle());
  }

  public String getDefaultOrientationY() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DEFAULT_ORIENTATION_Y.getCle());
  }

  /**
   * Par exemple, un tableau avec remocra:ADMINISTRATIF et remocra:RISQUE (initialement séparés par
   * un %)
   *
   * @return
   */
  public String[] getWmsPublicLayers() {
    return ((String) this.getValeur(ParamConf.ParamConfParam.WMS_PUBLIC_LAYERS.getCle()))
        .split("%");
  }

  public String getWmsBaseUrl() {
    return (String) this.getValeur(ParamConf.ParamConfParam.WMS_BASE_URL.getCle());
  }

  public String getCoordonneesFormatAffichage() {
    return (String) this.getValeur(ParamConf.ParamConfParam.COORDONNEES_FORMAT_AFFICHAGE.getCle());
  }

  public String getComplexitePassword() {
    return (String) this.getValeur(ParamConf.ParamConfParam.COMPLEXITE_PASSWORD.getCle());
  }

  public Integer getDeciDistanceMaxParcours() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.DECI_DISTANCE_MAX_PARCOURS.getCle());
  }

  public Integer getProfondeurCouverture() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.PROFONDEUR_COUVERTURE.getCle());
  }

  public String getDeciIsodistances() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DECI_ISODISTANCES.getCle());
  }

  public String getDashboardBaseUrl() {
    return (String) this.getValeur(ParamConf.ParamConfParam.DASHBOARD_BASE_URL.getCle());
  }

  public String getProcessOfflineUser() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PROCESS_OFFLINE_USER.getCle());
  }

  public Object getHydrantCfg() {
    JSONSerializer serializer = new JSONSerializer();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(
        "delai_reco_warn",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_DELAI_RECO_WARN.getCle()));
    data.put(
        "delai_reco_urgent",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_DELAI_RECO_URGENT.getCle()));
    data.put(
        "delai_rnvl_reco_prive",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PRIVE.getCle()));
    data.put(
        "delai_rnvl_reco_public",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_RECO_PUBLIC.getCle()));
    data.put(
        "delai_ctrl_warn",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_DELAI_CTRL_WARN.getCle()));
    data.put(
        "delai_ctrl_urgent",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_DELAI_CTRL_URGENT.getCle()));
    data.put(
        "delai_rnvl_ctrl_prive",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PRIVE.getCle()));
    data.put(
        "delai_rnvl_ctrl_public",
        this.getValeur(ParamConf.ParamConfParam.HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC.getCle()));

    return serializer.serialize(data);
  }

  public String getHydrantSymbologieMethode() {
    return (String) this.getValeur(ParamConf.ParamConfParam.HYDRANT_SYMBOLOGIE_METHODE.getCle());
  }

  // Sortie JWT
  public String getJwtOutPublicKey() {
    return (String) this.getValeur(ParamConf.ParamConfParam.SORTIE_JWT_CLEPUBLIQUE.getCle());
  }

  public String getJwtOutPrivateKey() {
    return (String) this.getValeur(ParamConf.ParamConfParam.SORTIE_JWT_CLEPRIVEE.getCle());
  }

  public String getJwtOutIssuer() {
    return (String) this.getValeur(ParamConf.ParamConfParam.SORTIE_JWT_ISSUER.getCle());
  }

  public Integer getJwtOutValidite() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.SORTIE_JWT_VALIDITE_SEC.getCle());
  }

  public String[] getHydrantColonnes() {
    return ((String) this.getValeur(ParamConf.ParamConfParam.HYDRANT_COLONNES.getCle())).split("%");
  }

  // LDAP

  public static enum LdapMethod {
    NONE,
    SIMPLE,
    SEARCHUSER
  }

  public LdapMethod getLdapMethod() {
    String host = getLdapUrlHost();
    if (host == null || host.isEmpty()) {
      return LdapMethod.NONE;
    }

    String userBaseName = getLdapUserBaseName();
    if (userBaseName == null || userBaseName.isEmpty()) {
      // Authentification LDAP basée sur couple username/password
      // Possible si dn utilisatetur = identifiant remocra
      return LdapMethod.SIMPLE;
    }

    // Etape de recherche de l'utilisateur pour obtenir son DN
    String dn = getLdapAdminDn();
    String password = getLdapAdminPassword();
    String userFilter = getLdapUserFilter();
    if (dn == null
        || dn.isEmpty()
        || password == null
        || password.isEmpty()
        || userFilter == null
        || userFilter.isEmpty()) {
      // Il manque les informations de connexion administrateur
      return LdapMethod.NONE;
    }
    return LdapMethod.SEARCHUSER;
  }

  public String getLdapUrlHost() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_URL_HOST.getCle());
  }

  public Integer getLdapUrlPort() {
    return (Integer) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_URL_PORT.getCle());
  }

  public String getLdapUrlBaseDn() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_URL_BASE_DN.getCle());
  }

  public String getLdapAdminDn() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_ADMIN_DN.getCle());
  }

  public String getLdapAdminPassword() {
    Password p =
        (Password) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_ADMIN_PASSWORD.getCle());
    return p == null ? null : p.toString();
  }

  public String getLdapUserBaseName() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_USER_BASE_NAME.getCle());
  }

  public String getLdapUserFilter() {
    return (String) this.getValeur(ParamConf.ParamConfParam.PDI_LDAP_USER_FILTER.getCle());
  }

  public String getLdapUrl() {
    String baseDn = getLdapUrlBaseDn();
    return "ldap://"
        + getLdapUrlHost()
        + ":"
        + getLdapUrlPort()
        + (baseDn == null || baseDn.isEmpty() ? "" : "/" + baseDn);
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

  public int getSridInt() {
    return Integer.parseInt(this.getValeurString(GlobalConstants.CLE_SRID));
  }

  public String getSridString() {
    return this.getValeurString(GlobalConstants.CLE_SRID);
  }
}
