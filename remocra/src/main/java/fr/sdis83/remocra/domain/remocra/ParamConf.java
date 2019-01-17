package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import fr.sdis83.remocra.domain.utils.Password;
import org.apache.commons.lang3.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(versionField = "", finders = { "findParamConfsByCleEquals" })
@SuppressWarnings("rawtypes")
public class ParamConf {
    
    // A noter : si un paramètre n'est pas listé ici, il est considéré comme
    // étant de "type" String
    public static enum ParamConfParam {
        // Second argument : Class avec un constructeur qui prend en paramètre
        // un String

        DOSSIER_DEPOT_ALERTE("DOSSIER_DEPOT_ALERTE", String.class),
        CITERNE_TOLERANCE_ASSOCIATION_PI_METRES("CITERNE_TOLERANCE_ASSOCIATION_PI_METRES", Integer.class),
        CLES_IGN("CLES_IGN", String.class),
        COMMUNES_INSEE_LIKE_FILTRE_SQL("COMMUNES_INSEE_LIKE_FILTRE_SQL", String.class),
        DEFAULT_ORIENTATION_X("DEFAULT_ORIENTATION_X", String.class),
        DEFAULT_ORIENTATION_Y("DEFAULT_ORIENTATION_Y", String.class),
        DOSSIER_RESSOURCES_EXTERNES("DOSSIER_RESSOURCES_EXTERNES", String.class),
        DOSSIER_DEPOT_DELIB("DOSSIER_DEPOT_DELIB", String.class),
        DOSSIER_DEPOT_DECLA_HYDRANT("DOSSIER_DEPOT_DECLA_HYDRANT", String.class),
        DOSSIER_DEPOT_RECEP_TRAVAUX("DOSSIER_DEPOT_RECEP_TRAVAUX", String.class),
        DOSSIER_DEPOT_PERMIS("DOSSIER_DEPOT_PERMIS", String.class),
        DOSSIER_DEPOT_RCI("DOSSIER_DEPOT_RCI", String.class),
        DOSSIER_DEPOT_BLOC("DOSSIER_DEPOT_BLOC", String.class),
        DOSSIER_GETFEATUREINFO("DOSSIER_GETFEATUREINFO", String.class),
        DOSSIER_LAYERS("DOSSIER_LAYERS", String.class),
        EMAIL_DEST_CREATION_RCI("EMAIL_DEST_CREATION_RCI", String.class),
        EMAIL_DEST_DEPOT_DELIB("EMAIL_DEST_DEPOT_DELIB", String.class),
        EMAIL_DEST_DEPOT_DECLAHYDRANT("EMAIL_DEST_DEPOT_DECLAHYDRANT", String.class),
        EMAIL_DEST_DEPOT_RECEPTRAVAUX("EMAIL_DEST_DEPOT_RECEPTRAVAUX", String.class),
        ID_TRAITEMENT_ATLAS("ID_TRAITEMENT_ATLAS", Integer.class),
        ID_TRAITEMENT_OLDEB("ID_TRAITEMENT_OLDEB", Integer.class),
        ID_TRAITEMENT_REQUETAGE("ID_TRAITEMENT_REQUETAGE", Integer.class ),
        ID_TRAITEMENT_PURGE_KML("ID_TRAITEMENT_PURGE_KML", Integer.class),
        ID_TRAITEMENT_HYDRANTS_NON_NUM("ID_TRAITEMENT_HYDRANTS_NON_NUM", Integer.class),
        ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR("ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR", Integer.class),
        MESSAGE_ENTETE("MESSAGE_ENTETE", String.class),
        TITRE_PAGE("TITRE_PAGE", String.class),
        MENTION_CNIL("MENTION_CNIL", String.class),
        PERMIS_TOLERANCE_CHARGEMENT_METRES("PERMIS_TOLERANCE_CHARGEMENT_METRES", Integer.class),
        TOLERANCE_VOIES_METRES("TOLERANCE_VOIES_METRES", Integer.class),
        PDI_CHEMIN_KML("PDI_CHEMIN_KML", String.class),
        PDI_CHEMIN_LOG("PDI_CHEMIN_LOG", String.class),
        PDI_CHEMIN_MODELES("PDI_CHEMIN_MODELES", String.class),
        PDI_CHEMIN_SYNCHRO("PDI_CHEMIN_SYNCHRO", String.class),
        PDI_CHEMIN_TMP("PDI_CHEMIN_TMP", String.class),
        PDI_CHEMIN_TRAITEMENT("PDI_CHEMIN_TRAITEMENT", String.class),
        PDI_DOSSIER_DEPOT("PDI_DOSSIER_DEPOT", String.class),
        PDI_DOSSIER_EXPORT_SDIS("PDI_DOSSIER_EXPORT_SDIS", String.class),
        PDI_FICHIER_PARAMETRAGE("PDI_FICHIER_PARAMETRAGE", String.class),
        PDI_DOSSIER_IMPORT_EXTRANET("PDI_DOSSIER_IMPORT_EXTRANET", String.class),
        PDI_FTP_DOSSIER_EXTRANET("PDI_FTP_DOSSIER_EXTRANET", String.class),
        PDI_FTP_DOSSIER_SDIS("PDI_FTP_DOSSIER_SDIS", String.class),
        PDI_FTP_DOSSIER_SYNCHRO("PDI_FTP_DOSSIER_SYNCHRO", String.class),
        PDI_NOM_SCHEMA_ORACLE("PDI_NOM_SCHEMA_ORACLE", String.class),
        PDI_FTP_PORT("PDI_FTP_PORT", Integer.class),
        PDI_FTP_URL("PDI_FTP_URL", String.class),
        PDI_FTP_USER_NAME("PDI_FTP_USER_NAME", String.class),
        PDI_FTP_USER_PASSWORD("PDI_FTP_USER_PASSWORD", Password.class),
        PDI_IMAP_PASSWORD("PDI_IMAP_PASSWORD", Password.class),
        PDI_IMAP_PORT("PDI_IMAP_PORT", Integer.class),
        PDI_IMAP_URL("PDI_IMAP_URL", String.class),
        PDI_IMAP_USER("PDI_IMAP_USER", String.class),
        PDI_METADATA_FILTRE_CQL("PDI_METADATA_FILTRE_CQL", String.class),
        PDI_METADATA_FILTRE_MAX("PDI_METADATA_FILTRE_MAX", Integer.class),
        PDI_METADATA_URL_FICHE_COMPLETE("PDI_METADATA_URL_FICHE_COMPLETE", String.class),
        PDI_METADATA_URL_GEOCATALOGUE("PDI_METADATA_URL_GEOCATALOGUE", String.class),
        PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID("PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID", Long.class),
        PDI_NOTIFICATION_GENERAL_MODELE_ID("PDI_NOTIFICATION_GENERAL_MODELE_ID", Integer.class),
        PDI_NOTIFICATION_KML_MODELE_ID("PDI_NOTIFICATION_KML_MODELE_ID", Integer.class),
        PDI_NOTIFICATION_KML_UTILISATEUR_ID("PDI_NOTIFICATION_KML_UTILISATEUR_ID", Integer.class),
        PDI_PDF_SIGN_KEY_PASSWORD("PDI_PDF_SIGN_KEY_PASSWORD", Password.class),
        PDI_PDF_SIGN_PFX_PASSWORD("PDI_PDF_SIGN_PFX_PASSWORD", Password.class),
        PDI_PDF_SIGN_PFX_FILE("PDI_PDF_SIGN_PFX_FILE", String.class),
        PDI_POSTGRESQL_NOM_SCHEMA_REFERENTIEL("PDI_POSTGRESQL_NOM_SCHEMA_REFERENTIEL", String.class),
        PDI_POSTGRESQL_NOM_SCHEMA_REMOCRA("PDI_POSTGRESQL_NOM_SCHEMA_REMOCRA", String.class),
        PDI_POSTGRESQL_NOM_SCHEMA_SYNCHRO("PDI_POSTGRESQL_NOM_SCHEMA_SYNCHRO", String.class),
        PDI_PURGE_ALERTE_JOURS("PDI_PURGE_ALERTE_JOURS", Integer.class),
        PDI_PURGE_MAIL_JOURS("PDI_PURGE_MAIL_JOURS", Integer.class),
        PDI_PURGE_TRAITEMENT_JOURS("PDI_PURGE_TRAITEMENT_JOURS", Integer.class),
        PDI_SMTP_EME_MAIL("PDI_SMTP_EME_MAIL", String.class),
        PDI_SMTP_EME_NAME("PDI_SMTP_EME_NAME", String.class),
        PDI_SMTP_PASSWORD("PDI_SMTP_PASSWORD", Password.class),
        PDI_SMTP_PORT("PDI_SMTP_PORT", Integer.class),
        PDI_SMTP_REP_MAIL("PDI_SMTP_REP_MAIL", String.class),
        PDI_SMTP_URL("PDI_SMTP_URL", String.class),
        PDI_SMTP_USER("PDI_SMTP_USER", String.class),
        PDI_URL_SITE("PDI_URL_SITE", String.class),
        DOSSIER_DOC_HYDRANT("DOSSIER_DOC_HYDRANT", String.class),
        DOSSIER_DOC_OLDEBVISITE("DOSSIER_DOC_OLDEBVISITE", String.class),
        HYDRANT_DELAI_RECO_WARN("HYDRANT_DELAI_RECO_WARN", Integer.class),
        HYDRANT_DELAI_RECO_URGENT("HYDRANT_DELAI_RECO_URGENT", Integer.class),
        HYDRANT_DELAI_CTRL_WARN("HYDRANT_DELAI_CTRL_WARN", Integer.class),
        HYDRANT_DELAI_CTRL_URGENT("HYDRANT_DELAI_CTRL_URGENT", Integer.class),
        HYDRANT_NUMEROTATION_INTERNE_METHODE("HYDRANT_NUMEROTATION_INTERNE_METHODE", String.class),
        HYDRANT_NUMEROTATION_METHODE("HYDRANT_NUMEROTATION_METHODE", String.class),
        HYDRANT_RENUMEROTATION_ACTIVATION("HYDRANT_RENUMEROTATION_ACTIVATION", Boolean.class),
        HYDRANT_SYMBOLOGIE_METHODE("HYDRANT_SYMBOLOGIE_METHODE", String.class),
        HYDRANT_RENOUVELLEMENT_RECO("HYDRANT_RENOUVELLEMENT_RECO", Integer.class),
        HYDRANT_RENOUVELLEMENT_CTRL("HYDRANT_RENOUVELLEMENT_CTRL", Integer.class),
        HYDRANT_NOMBRE_HISTORIQUE("HYDRANT_NOMBRE_HISTORIQUE", Integer.class),
        HYDRANT_VISITE_RAPIDE("HYDRANT_VISITE_RAPIDE", Boolean.class),
        HYDRANT_HIGHLIGHT_DUREE("HYDRANT_HIGHLIGHT_DUREE", Integer.class),
        WMS_BASE_URL("WMS_BASE_URL", String.class),
        WMS_PUBLIC_LAYERS("WMS_PUBLIC_LAYERS", String.class),
        COORDONNEES_FORMAT_AFFICHAGE("COORDONNEES_FORMAT_AFFICHAGE", String.class),
        // JWT
        SORTIE_JWT_VALIDITE_SEC("SORTIE_JWT_VALIDITE_SEC", Integer.class),
        SORTIE_JWT_CLEPRIVEE("SORTIE_JWT_CLEPRIVEE", String.class),
        SORTIE_JWT_CLEPUBLIQUE("SORTIE_JWT_CLEPUBLIQUE", String.class),
        SORTIE_JWT_ISSUER("SORTIE_JWT_ISSUER", String.class),
        // LDAP
        PDI_LDAP_URL_HOST("PDI_LDAP_URL_HOST", String.class),
        PDI_LDAP_URL_PORT("PDI_LDAP_URL_PORT", Integer.class),
        PDI_LDAP_URL_BASE_DN("PDI_LDAP_URL_BASE_DN", String.class),
        PDI_LDAP_ADMIN_DN("PDI_LDAP_ADMIN_DN", String.class),
        PDI_LDAP_ADMIN_PASSWORD("PDI_LDAP_ADMIN_PASSWORD", Password.class),
        PDI_LDAP_USER_BASE_NAME("PDI_LDAP_USER_BASE_NAME", String.class),
        PDI_LDAP_USER_FILTER("PDI_LDAP_USER_FILTER", String.class);


        private final String cle;
        private final Class cl;

        ParamConfParam(String cle, Class cl) {
            this.cle = cle;
            this.cl = cl;
        }

        public String getCle() {
            return this.cle;
        }

        public Class getCl() {
            return this.cl;
        }

        public static Class getClassFromCle(String cle) {
            if (StringUtils.isEmpty(cle)) {
                return null;
            }
            for (ParamConfParam o : ParamConfParam.values()) {
                if (o.getCle().toLowerCase().equals(cle.toLowerCase()))
                    return o.getCl();
            }
            return String.class;
        }

    }

    @Id
    private String cle;

    @NotNull
    private String valeur;

    private String description;

    @NotNull
    private String nomgroupe;
    
    public String getClDisplay() {
        return ParamConfParam.getClassFromCle(cle).getSimpleName();
    }
}
